package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.Battle;
import it.polimi.codekatabattle.entities.BattleEntry;
import it.polimi.codekatabattle.entities.BattleEntryStatus;
import it.polimi.codekatabattle.models.BattleEntryProcessResult;
import it.polimi.codekatabattle.models.BattleTestResult;
import it.polimi.codekatabattle.models.ExecuteResult;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final BattleEntryRepository battleEntryRepository;

    Logger logger = LoggerFactory.getLogger(ScoreServiceImpl.class);

    public ScoreServiceImpl(BattleEntryRepository battleEntryRepository) {
        this.battleEntryRepository = battleEntryRepository;
    }

    @Override
    @Async
    public CompletableFuture<BattleEntry> processBattleEntry(BattleEntry battleEntry, URL artifactUrl) {
        List<BattleTestResult> testResults = this.executeBattleTests(battleEntry, artifactUrl);
        Optional<SATResult> satResult = this.executeSAT(battleEntry, artifactUrl);

        BattleEntryProcessResult processResult = new BattleEntryProcessResult();
        processResult.setTestResults(testResults);
        processResult.setSatResult(satResult.orElse(null));

        battleEntry.setProcessResult(processResult);

        if (testResults.stream().anyMatch(tr -> tr.isTimeout() || tr.getExitCode() != 0)) {
            battleEntry.setStatus(BattleEntryStatus.COMPLETED_WITH_ERRORS);
        } else {
            battleEntry.setStatus(BattleEntryStatus.COMPLETED);
        }

        // Calculate score and save it in battle entry
        int score = testResults.stream().mapToInt(BattleTestResult::getScore).sum() + (satResult.map(SATResult::getScore).orElse(0));

        // Apply timeliness bonus
        if (battleEntry.getBattle().getTimelinessBaseScore() > 0) {
            // Calculation is based on the number of days from the battle creation to the submission. Each day passed subtracts 10 points from the base timeliness score
            score += Math.max(0, battleEntry.getBattle().getTimelinessBaseScore() - Math.abs((int)battleEntry.getBattle().getCreatedAt().until(battleEntry.getCreatedAt(), ChronoUnit.DAYS)) * 10);
        }

        battleEntry.setScore(score);
        this.battleEntryRepository.save(battleEntry);

        return CompletableFuture.completedFuture(battleEntry);
    }

    @Override
    public List<BattleTestResult> executeBattleTests(BattleEntry battleEntry, URL artifactUrl) {
        battleEntry.setStatus(BattleEntryStatus.TESTING);
        this.battleEntryRepository.save(battleEntry);

        Battle battle = battleEntry.getBattle();
        ExecutorService executor = this.getBattleExecutor(battle);

        List<BattleTestResult> results = battle.getTests().parallelStream().map(test -> {
            logger.info("Executing test " + test.getName() + " for battle " + battle.getId() + " and artifact " + artifactUrl.toString());
            BattleTestResult btr = new BattleTestResult();
            btr.setName(test.getName());
            btr.setInput(test.getInput());

            try {
                // Execute code with timeout
                ExecuteResult result = executor.executeArtifact(artifactUrl, test.getInput()).get(30, TimeUnit.SECONDS);
                boolean passed = test.getExpectedOutput().equals(result.getOutput());

                btr.setOutput(result.getOutput());
                btr.setExitCode(result.getExitCode().intValue());
                btr.setError(result.getError());
                btr.setPassed(passed);
                btr.setTimeout(false);
                btr.setScore(passed ? test.getGivesScore() : 0);
                logger.info("Done executing test " + test.getName() + " for battle " + battle.getId() + " and artifact " + artifactUrl + ": " + (passed ? "passed" : "failed") + " with exit code " + result.getExitCode() + " and output " + result.getOutput());
            } catch (ExecutionException | InterruptedException ex) {
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
            } catch (Exception ex) {
                btr.setPassed(false);
                btr.setTimeout(false);
                btr.setExitCode(-1);
                btr.setError(ex.getMessage());
                logger.error("Got Exception while executing test " + test.getName() + " for battle " + battle.getId() + " and artifact " + artifactUrl + ": " + ex.getMessage());
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

        ExecutorService executor = this.getBattleExecutor(battleEntry.getBattle());
        try {
            ExecuteResult result = executor.executeSAT(artifactUrl).get(30, TimeUnit.SECONDS);
            if (result.getExitCode() != 0) {
                logger.error("Got non-zero exit code while executing SAT for battle " + battleEntry.getBattle().getId() + " and artifact " + artifactUrl + ": " + result.getExitCode());
                return Optional.empty();
            }

            SATResult satResult = new SATResult();
            satResult.setSatName(executor.getSATName());
            satResult.setWarnings(List.of(result.getOutput().split("\n")));
            satResult.setScore(Math.max(0, 10 - result.getOutput().split("\n").length));

            logger.info("Done executing SAT for battle " + battleEntry.getBattle().getId() + " and artifact " + artifactUrl + ": " + satResult.getScore());
            return Optional.of(satResult);
        } catch (Exception e) {
            logger.error("Got exception while executing SAT for battle " + battleEntry.getBattle().getId() + " and artifact " + artifactUrl + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private ExecutorService getBattleExecutor(Battle battle) {
        return switch (battle.getLanguage()) {
            case GOLANG -> new GolangExecutorService();
            case PYTHON -> new PythonExecutorService();
        };
    }

}
