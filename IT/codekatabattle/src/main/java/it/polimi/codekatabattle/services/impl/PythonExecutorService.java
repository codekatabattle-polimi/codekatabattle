package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.models.ExecuteResult;
import it.polimi.codekatabattle.services.ExecutorService;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PythonExecutorService extends BaseExecutorService implements ExecutorService {

    @Override
    @Async
    public CompletableFuture<ExecuteResult> executeArtifact(URL artifactUrl, String input) throws ExecutionException {
        try {
            String artifactsPath = prepareArtifacts(artifactUrl, input);

            try (GenericContainer<?> codeContainer = new GenericContainer<>(DockerImageName.parse("python:3.12-alpine"))
                .withCopyToContainer(MountableFile.forHostPath(artifactsPath), "/submission")
                .withWorkingDirectory("/submission")
                .withCommand("python main.py")
                .withStartupCheckStrategy(
                    new OneShotStartupCheckStrategy().withTimeout(Duration.ofMinutes(1))
                )) {
                codeContainer.start();
                return CompletableFuture.completedFuture(ExecuteResult.fromContainer(codeContainer));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public CompletableFuture<ExecuteResult> executeSAT(URL artifactUrl) throws ExecutionException {
        // Execute Pylint on every Python file and return the result
        try {
            String artifactsPath = prepareArtifacts(artifactUrl, "");

            try (GenericContainer<?> satContainer = new GenericContainer<>(DockerImageName.parse("registry.gitlab.com/pipeline-components/pylint:latest"))
                .withCopyToContainer(MountableFile.forHostPath(artifactsPath), "/submission")
                .withWorkingDirectory("/submission")
                .withCommand("/app/pylint **/*.py")
                .withStartupCheckStrategy(
                    new OneShotStartupCheckStrategy().withTimeout(Duration.ofMinutes(1))
                )) {
                satContainer.start();
                return CompletableFuture.completedFuture(ExecuteResult.fromContainer(satContainer));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public String getSATName() {
        return "Pylint";
    }

}
