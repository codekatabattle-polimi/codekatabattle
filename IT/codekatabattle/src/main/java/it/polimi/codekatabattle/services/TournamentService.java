package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;

public interface TournamentService extends CrudService<Tournament> {

    Tournament create(@Valid @NotNull TournamentDTO tournament) throws ValidationException;

}
