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

    @Override
    @Async
    public CompletableFuture<Container.ExecResult> execute(URL artifactUrl, String input) throws ExecutionException, IOException {
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
        // TODO: Implement SAT for Python (Pylint)
        return null;
    }

    @Override
    public String getSATName() {
        return "Pylint";
    }

}
