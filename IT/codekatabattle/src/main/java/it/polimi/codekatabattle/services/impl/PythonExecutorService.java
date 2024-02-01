package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.services.ExecutorService;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.Container;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PythonExecutorService implements ExecutorService {

    @Override
    public CompletableFuture<Container.ExecResult> execute(URL artifactUrl, String input) throws ExecutionException, IOException {
        return null;
    }

}
