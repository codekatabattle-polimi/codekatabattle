package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.models.ExecuteResult;
import it.polimi.codekatabattle.services.ExecutorService;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GolangExecutorService extends BaseExecutorService implements ExecutorService {

    @Override
    @Async
    public CompletableFuture<ExecuteResult> executeArtifact(URL artifactUrl, String input) throws ExecutionException {
        try {
            String artifactsPath = prepareArtifacts(artifactUrl, input);

            try (GenericContainer<?> golangContainer = new GenericContainer<>(DockerImageName.parse("golang:1.21-alpine"))
                .withCopyToContainer(MountableFile.forHostPath(artifactsPath), "/submission")
                .withWorkingDirectory("/submission")
                .withCommand("go run kata.go")
                .withStartupCheckStrategy(
                    new OneShotStartupCheckStrategy().withTimeout(Duration.ofMinutes(1))
                )) {
                golangContainer.start();
                return CompletableFuture.completedFuture(ExecuteResult.fromContainer(golangContainer));
            }
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public CompletableFuture<ExecuteResult> executeSAT(URL artifactUrl) throws ExecutionException {
        // Execute staticcheck on every go file and return the result
        try {
            String artifactsPath = prepareArtifacts(artifactUrl, "");

            try (GenericContainer<?> satContainer = new GenericContainer<>(DockerImageName.parse("devdrops/staticcheck:latest"))
                .withCopyToContainer(MountableFile.forHostPath(artifactsPath), "/submission")
                .withWorkingDirectory("/submission")
                .withCommand("staticcheck ./...")
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
        return "StaticCheck";
    }

}
