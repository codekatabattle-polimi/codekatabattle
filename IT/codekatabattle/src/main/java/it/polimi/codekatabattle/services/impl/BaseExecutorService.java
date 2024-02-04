package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.utils.ZipUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Objects;

public class BaseExecutorService {

    public void copyArtifactsInContainer(GenericContainer<?> container, URL artifactUrl, String input) throws IOException {
        // Download zip archive from artifactUrl and extract it to a temp directory
        String artifactsPath = ZipUtils.downloadAndExtractToTempDirectory(artifactUrl);

        // Add the input.txt file to the extracted files
        if (!Objects.equals(input, "")) {
            try (PrintWriter writer = new PrintWriter(artifactsPath + "input.txt")) {
                writer.write(input);
            }
        }

        // Copy the files from the extracted archive to the golang container
        // Iterate through files
        File[] files = new File(artifactsPath).listFiles();
        if (files == null) {
            throw new IOException("No files found in the artifact");
        }
        for (File file : files) {
            if (file.isFile()) {
                container.copyFileToContainer(MountableFile.forHostPath(file.getAbsolutePath()), "/app");
            }
        }
    }

}
