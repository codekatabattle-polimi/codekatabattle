package it.polimi.codekatabattle.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BattleEntryDTO {

    @NotBlank
    private String artifactUrl;

}
