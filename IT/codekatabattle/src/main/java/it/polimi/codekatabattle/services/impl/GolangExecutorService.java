package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.services.ExecutorService;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GolangExecutorService implements ExecutorService {

    private static final GenericContainer<?> golangContainer = new GenericContainer<>(DockerImageName.parse("go:1.21-alpine"));

    @Override
    @Async
    public CompletableFuture<Container.ExecResult> execute(URL artifactUrl, String input) throws ExecutionException, IOException {
        // Download zip archive from artifactUrl and extract it to a temp directory
        String artifactsPath = this.downloadAndExtractArtifactsToTempDirectory(artifactUrl);

        // Add the input.txt file to the extracted files
        try (PrintWriter writer = new PrintWriter(artifactsPath + "input.txt")) {
            writer.write(input);
        }

        // Copy the files from the extracted archive to the golang container
        // Iterate through files
        File[] files = new File(artifactsPath).listFiles();
        if (files == null) {
            throw new IOException("No files found in the artifact");
        }
        for (File file : files) {
            if (file.isFile()) {
                golangContainer.copyFileToContainer(MountableFile.forHostPath(file.getAbsolutePath()), "/app");
            }
        }

        // Execute the main.go file in the golang container and return the result
        try {
            golangContainer.start();
            Container.ExecResult result = golangContainer.execInContainer("go run main.go");
            return CompletableFuture.completedFuture(result);
        } catch (IOException | InterruptedException e) {
            throw new ExecutionException(e);
        } finally {
            golangContainer.stop();
        }
    }

    private String downloadAndExtractArtifactsToTempDirectory(URL artifactUrl) throws IOException {
        String zipFilePath = System.getProperty("java.io.tmpdir") + "/artifact.zip";
        String extractedFilesPath = System.getProperty("java.io.tmpdir") + "/artifact_" + UUID.randomUUID() + "/";

        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath)) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            ReadableByteChannel readableByteChannel = Channels.newChannel(artifactUrl.openStream());

            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        extractZipFile(zipFilePath, extractedFilesPath);
        return extractedFilesPath;
    }

    private void extractZipFile(String zipFilePath, String destDir) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            File newFile = newFile(new File(destDir), zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // Fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // Write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
