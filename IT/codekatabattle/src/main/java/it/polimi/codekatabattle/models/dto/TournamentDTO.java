package it.polimi.codekatabattle.models.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentDTO {

    @NotBlank
    @Max(100)
    private String title;

    @NotBlank
    @Max(5000)
    private String description;

    @Future
    private LocalDateTime startsAt;

    @Future
    private LocalDateTime endsAt;

}
