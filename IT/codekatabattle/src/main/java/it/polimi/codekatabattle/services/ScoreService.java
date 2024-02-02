package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.entities.BattleEntry;
import it.polimi.codekatabattle.models.BattleEntryProcessResult;
import it.polimi.codekatabattle.models.BattleTestResult;
import it.polimi.codekatabattle.models.SATResult;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ScoreService {

    CompletableFuture<BattleEntryProcessResult> processBattleEntry(BattleEntry battleEntry, URL artifactUrl);

    List<BattleTestResult> executeBattleTests(BattleEntry battleEntry, URL artifactUrl);

    Optional<SATResult> executeSAT(BattleEntry battleEntry, URL artifactUrl);

}
