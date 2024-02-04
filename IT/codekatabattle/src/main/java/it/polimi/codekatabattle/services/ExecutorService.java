package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.models.ExecuteResult;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface ExecutorService {

    CompletableFuture<ExecuteResult> executeArtifact(URL artifactUrl, String input) throws ExecutionException;

    CompletableFuture<ExecuteResult> executeSAT(URL artifactUrl) throws ExecutionException;

    String getSATName();

}
