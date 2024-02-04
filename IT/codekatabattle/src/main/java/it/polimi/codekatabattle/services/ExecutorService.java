package it.polimi.codekatabattle.services;

import org.testcontainers.containers.Container;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface ExecutorService {

    CompletableFuture<Container.ExecResult> execute(URL artifactUrl, String input) throws ExecutionException, IOException;

    CompletableFuture<Container.ExecResult> executeSAT(URL artifactUrl) throws ExecutionException, IOException;

    String getSATName();

}
