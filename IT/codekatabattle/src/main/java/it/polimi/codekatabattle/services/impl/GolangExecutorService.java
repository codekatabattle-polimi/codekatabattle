package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.services.ExecutorService;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GolangExecutorService implements ExecutorService {

    private static final GenericContainer<?> golangContainer = new GenericContainer<>(DockerImageName.parse("go:1.21-alpine"));

    @Override
    @Async
    public CompletableFuture<Container.ExecResult> execute(URL artifactUrl, String input) throws ExecutionException, IOException {
        // Download zip archive from artifactUrl and extract it to a temp directory
        String artifactsPath = this.downloadAndExtractArtifactsToTempDirectory(artifactUrl);

        // Copy the files from the extracted archive to the golang container

        // Add the input.txt file

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

        return extractedFilesPath;
    }

    private void extractWithZipInputStream() {

    }

}
