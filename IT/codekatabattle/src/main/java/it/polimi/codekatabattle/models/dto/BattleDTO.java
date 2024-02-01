package it.polimi.codekatabattle.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.polimi.codekatabattle.entities.Battle;
import it.polimi.codekatabattle.entities.BattleLanguage;
import it.polimi.codekatabattle.entities.BattleTest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.codekatabattle.config.APIConstants.DATETIME_FORMAT;

@Data
public class BattleDTO {

    @NotNull
    private Long tournamentId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Future
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime startsAt;

    @Future
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime endsAt;

    @NotNull
    private BattleLanguage language;

    List<BattleTest> tests = new ArrayList<>();

    private Boolean enableSAT = false;

    private Boolean enableManualEvaluation = false;

    private int timelinessBaseScore = 0;

    public Battle toEntity() {
        Battle battle = new Battle();
        battle.setTitle(title);
        battle.setDescription(description);
        battle.setStartsAt(startsAt);
        battle.setEndsAt(endsAt);
        battle.setLanguage(language);
        battle.setEnableSAT(enableSAT);
        battle.setTests(tests);
        battle.setTimelinessBaseScore(timelinessBaseScore);
        return battle;
    }

}
