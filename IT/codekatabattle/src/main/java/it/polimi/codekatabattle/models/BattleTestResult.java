package it.polimi.codekatabattle.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class BattleTestResult {
    @NotBlank
    private String name;

    @NotBlank
    private String input;

    private String output;

    private String error;

    @NotNull
    private Integer exitCode;

    @NotNull
    private boolean timeout;

    @NotNull
    private boolean passed;

    @PositiveOrZero
    private Integer score = 0;
}

