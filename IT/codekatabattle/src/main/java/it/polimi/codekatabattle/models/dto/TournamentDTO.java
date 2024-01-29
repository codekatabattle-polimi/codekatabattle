package it.polimi.codekatabattle.models.dto;

import it.polimi.codekatabattle.entities.Tournament;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Future
    private LocalDateTime startsAt;

    @Future
    private LocalDateTime endsAt;

    public Tournament toEntity() {
        Tournament tournament = new Tournament();
        tournament.setTitle(title);
        tournament.setDescription(description);
        tournament.setStartsAt(startsAt);
        tournament.setEndsAt(endsAt);
        return tournament;
    }

}
