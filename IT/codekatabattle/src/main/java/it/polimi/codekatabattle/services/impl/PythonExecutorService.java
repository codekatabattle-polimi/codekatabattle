package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.services.ExecutorService;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PythonExecutorService extends BaseExecutorService implements ExecutorService {

    private static final GenericContainer<?> pythonContainer = new GenericContainer<>(DockerImageName.parse("python:3.12-alpine"));

    private static final GenericContainer<?> pylintContainer = new GenericContainer<>(DockerImageName.parse("registry.gitlab.com/pipeline-components/pylint:latest"));

    @Override
    @Async
    public CompletableFuture<Container.ExecResult> executeArtifact(URL artifactUrl, String input) throws ExecutionException, IOException {
        copyArtifactsInContainer(pythonContainer, artifactUrl, input);

        // Execute the main.py file in the Python container and return the result
        try {
            pythonContainer.start();
            Container.ExecResult result = pythonContainer.execInContainer("python main.py");
            return CompletableFuture.completedFuture(result);
        } catch (IOException | InterruptedException e) {
            throw new ExecutionException(e);
        } finally {
            pythonContainer.stop();
        }
    }

    @Override
    public CompletableFuture<Container.ExecResult> executeSAT(URL artifactUrl) throws ExecutionException, IOException {
        copyArtifactsInContainer(pylintContainer, artifactUrl, "");

        // Execute staticcheck on the main.go file and return the result
        try {
            pylintContainer.start();
            Container.ExecResult result = pylintContainer.execInContainer("/app/pylint **/*.py");
            return CompletableFuture.completedFuture(result);
        } catch (IOException | InterruptedException e) {
            throw new ExecutionException(e);
        } finally {
            pylintContainer.stop();
        }
    }

    @Override
    public String getSATName() {
        return "Pylint";
    }

}
