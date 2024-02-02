package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.Battle;
import it.polimi.codekatabattle.entities.BattleEntry;
import it.polimi.codekatabattle.entities.BattleEntryStatus;
import it.polimi.codekatabattle.models.BattleEntryProcessResult;
import it.polimi.codekatabattle.models.BattleTestResult;
import it.polimi.codekatabattle.models.SATResult;
import it.polimi.codekatabattle.repositories.BattleEntryRepository;
import it.polimi.codekatabattle.services.ExecutorService;
import it.polimi.codekatabattle.services.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.Container;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final BattleEntryRepository battleEntryRepository;

    Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);

    public ScoreServiceImpl(BattleEntryRepository battleEntryRepository) {
        this.battleEntryRepository = battleEntryRepository;
    }

    @Override
    @Async
    public CompletableFuture<BattleEntryProcessResult> processBattleEntry(BattleEntry battleEntry, URL artifactUrl) {
        List<BattleTestResult> testResults = this.executeBattleTests(battleEntry, artifactUrl);
        Optional<SATResult> satResult = this.executeSAT(battleEntry, artifactUrl);

        BattleEntryProcessResult results = new BattleEntryProcessResult();
        results.setTestResults(testResults);
        results.setSatResult(satResult.orElse(null));

        return CompletableFuture.completedFuture(results);
    }

    @Override
    public List<BattleTestResult> executeBattleTests(BattleEntry battleEntry, URL artifactUrl) {
        battleEntry.setStatus(BattleEntryStatus.TESTING);
        this.battleEntryRepository.save(battleEntry);

        Battle battle = battleEntry.getBattle();
        ExecutorService executor = this.getBattleCodeExecutor(battle);

        List<BattleTestResult> results = battle.getTests().parallelStream().map(test -> {
            logger.info("Executing test " + test.getName() + " for battle " + battle.getId() + " and artifact " + artifactUrl.toString());
            BattleTestResult btr = new BattleTestResult();
            btr.setName(test.getName());
            btr.setInput(test.getInput());

            try {
                // Execute code with timeout
                Container.ExecResult result = executor.execute(artifactUrl, test.getInput()).get(30, TimeUnit.SECONDS);
                boolean passed = test.getExpectedOutput().equals(result.getStdout());

                btr.setOutput(result.getStdout());
                btr.setExitCode(result.getExitCode());
                btr.setError(result.getStderr());
                btr.setPassed(passed);
                btr.setTimeout(false);
                logger.info("Done executing test " + test.getName() + " for battle " + battle.getId() + " and artifact " + artifactUrl + ": " + (passed ? "passed" : "failed") + " with exit code " + result.getExitCode() + " and output " + result.getStdout());
            } catch (ExecutionException | InterruptedException | IOException ex) {
                btr.setPassed(false);
                btr.setTimeout(false);
                btr.setExitCode(-1);
                btr.setError(ex.getMessage());
                logger.error("Got ExecutionException while executing test " + test.getName() + " for battle " + battle.getId() + " and artifact " + artifactUrl + ": " + ex.getMessage());
            } catch (TimeoutException ex) {
                btr.setPassed(false);
                btr.setTimeout(true);
                btr.setExitCode(-1);
                btr.setError("Timeout");
                logger.error("Got TimeoutException while executing test " + test.getName() + " for battle " + battle.getId() + " and artifact " + artifactUrl + ": " + ex.getMessage());
            }

            return btr;
        }).toList();

        logger.info("Performed all " + results.size() + " tests for battle " + battle.getId() + " and artifact " + artifactUrl.toString());
        return results;
    }

    @Override
    public Optional<SATResult> executeSAT(BattleEntry battleEntry, URL artifactUrl) {
        if (!battleEntry.getBattle().getEnableSAT()) {
            return Optional.empty();
        }

        battleEntry.setStatus(BattleEntryStatus.ANALYZING);
        this.battleEntryRepository.save(battleEntry);

        // TODO: Implement SAT execution
        return Optional.empty();
    }

    private ExecutorService getBattleCodeExecutor(Battle battle) {
        return switch (battle.getLanguage()) {
            case GOLANG -> new GolangExecutorService();
            case PYTHON -> new PythonExecutorService();
        };
    }

}
