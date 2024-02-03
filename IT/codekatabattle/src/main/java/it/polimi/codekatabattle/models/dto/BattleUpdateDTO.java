package it.polimi.codekatabattle.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

import static it.polimi.codekatabattle.config.APIConstants.DATETIME_FORMAT;

@Data
public class BattleUpdateDTO {

    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime startsAt;

    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime endsAt;

    private Boolean enableManualEvaluation = false;

}
