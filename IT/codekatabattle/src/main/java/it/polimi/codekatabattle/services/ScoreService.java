package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.entities.BattleEntry;
import it.polimi.codekatabattle.entities.BattleTestResult;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ScoreService {

    CompletableFuture<List<BattleTestResult>> processBattleEntry(BattleEntry battleEntry, URL artifactUrl);

}
