package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;

public interface TournamentService extends CrudService<Tournament> {

    Tournament create(TournamentDTO tournament, GHUser creator) throws ValidationException;

    Tournament join(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException;

    Tournament leave(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException;

    Tournament updateById(Long tournamentId, TournamentDTO tournament, GHUser updater) throws EntityNotFoundException, ValidationException;

    Tournament deleteById(Long tournamentId, GHUser deleter) throws EntityNotFoundException, ValidationException;

}
