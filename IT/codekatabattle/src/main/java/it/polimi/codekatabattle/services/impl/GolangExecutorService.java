package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.services.ExecutorService;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.*;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GolangExecutorService extends BaseExecutorService implements ExecutorService {

    private static final GenericContainer<?> golangContainer = new GenericContainer<>(DockerImageName.parse("go:1.21-alpine"));

    private static final GenericContainer<?> staticcheckContainer = new GenericContainer<>(DockerImageName.parse("tbcloud/staticcheck:11"));

    @Override
    @Async
    public CompletableFuture<Container.ExecResult> execute(URL artifactUrl, String input) throws ExecutionException, IOException {
        copyArtifactsInContainer(golangContainer, artifactUrl, input);

        // Execute the main.go file in the Golang container and return the result
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

    @Override
    public CompletableFuture<Container.ExecResult> executeSAT(URL artifactUrl) throws ExecutionException, IOException {
        copyArtifactsInContainer(staticcheckContainer, artifactUrl, "");

        // Execute staticcheck on the main.go file and return the result
        try {
            golangContainer.start();
            Container.ExecResult result = golangContainer.execInContainer("staticcheck main.go");
            return CompletableFuture.completedFuture(result);
        } catch (IOException | InterruptedException e) {
            throw new ExecutionException(e);
        } finally {
            golangContainer.stop();
        }
    }

    @Override
    public String getSATName() {
        return "StaticCheck";
    }

}
