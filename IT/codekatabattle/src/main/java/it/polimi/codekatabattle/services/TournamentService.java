package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TournamentService {

    Tournament create(TournamentDTO tournament, GHUser creator) throws ValidationException;

    Tournament findById(Long tournamentId) throws EntityNotFoundException;

    Page<Tournament> findAllPublic(Pageable pageable);

    Page<Tournament> findAllByCreator(Pageable pageable, String creator);

    Page<Tournament> findAllByParticipant(Pageable pageable, String participant);

    Page<Tournament> findAllByCoordinator(Pageable pageable, String coordinator);

    Tournament join(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException;

    Tournament leave(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException;

    Tournament updateById(Long tournamentId, TournamentDTO tournament, GHUser updater) throws EntityNotFoundException, ValidationException;

    Tournament deleteById(Long tournamentId, GHUser deleter) throws EntityNotFoundException, ValidationException;

}
