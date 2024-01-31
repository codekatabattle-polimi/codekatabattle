package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.entities.Battle;

public interface ScoreService {

    int scoreArtifact(String artifactUrl, Battle battle);

}
