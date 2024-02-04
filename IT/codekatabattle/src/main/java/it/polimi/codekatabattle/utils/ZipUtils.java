package it.polimi.codekatabattle.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static String downloadAndExtractToTempDirectory(URL artifactUrl) throws IOException {
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

    public static void extractZipFile(String zipFilePath, String destDir) throws IOException {
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

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
