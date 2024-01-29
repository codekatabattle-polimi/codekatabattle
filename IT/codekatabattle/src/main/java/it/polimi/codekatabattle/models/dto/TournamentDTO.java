package it.polimi.codekatabattle.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.entities.TournamentPrivacy;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

import static it.polimi.codekatabattle.config.APIConstants.DATETIME_FORMAT;

@Data
public class TournamentDTO {

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
    private TournamentPrivacy privacy = TournamentPrivacy.PUBLIC;

    private Integer maxParticipants;

    public Tournament toEntity() {
        Tournament tournament = new Tournament();
        tournament.setTitle(title);
        tournament.setDescription(description);
        tournament.setStartsAt(startsAt);
        tournament.setEndsAt(endsAt);
        tournament.setPrivacy(privacy);
        tournament.setMaxParticipants(maxParticipants);
        return tournament;
    }

}
