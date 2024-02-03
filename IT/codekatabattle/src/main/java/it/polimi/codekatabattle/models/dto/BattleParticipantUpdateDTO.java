package it.polimi.codekatabattle.models.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class BattleParticipantUpdateDTO {

    @PositiveOrZero
    private Integer score;

}
