package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.models.ExecuteResult;

public interface ExecutorService {

    ExecuteResult execute(String artifactUrl, String input);

}
