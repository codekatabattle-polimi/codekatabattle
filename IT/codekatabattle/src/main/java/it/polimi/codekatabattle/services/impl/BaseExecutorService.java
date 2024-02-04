package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.utils.ZipUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Objects;

public class BaseExecutorService {

    public String prepareArtifacts(URL artifactUrl, String input) throws IOException {
        // Download zip archive from artifactUrl and extract it to a temp directory
        String artifactsPath = ZipUtils.downloadAndExtractToTempDirectory(artifactUrl);

        // Add the input.txt file to the extracted files
        if (!Objects.equals(input, "")) {
            try (PrintWriter writer = new PrintWriter(artifactsPath + "input.txt")) {
                writer.write(input);
            }
        }

        return artifactsPath;
    }

}
