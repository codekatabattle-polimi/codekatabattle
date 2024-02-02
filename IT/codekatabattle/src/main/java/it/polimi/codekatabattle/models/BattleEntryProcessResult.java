package it.polimi.codekatabattle.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BattleEntryProcessResult {

    @NotNull
    private List<BattleTestResult> testResults = new ArrayList<>();

    private SATResult satResult = null;

}
