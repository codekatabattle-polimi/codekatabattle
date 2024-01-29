package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.TournamentParticipant;
import it.polimi.codekatabattle.entities.Tournament;
import it.polimi.codekatabattle.models.dto.TournamentDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.repositories.TournamentRepository;
import it.polimi.codekatabattle.services.TournamentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class TournamentServiceImpl extends CrudServiceImpl<Tournament> implements TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        super(tournamentRepository);
        this.tournamentRepository = tournamentRepository;
    }

    public Tournament create(@Valid @NotNull TournamentDTO tournament) throws ValidationException {
        return this.save(tournament.toEntity());
    }

    @Override
    @Transactional
    public Tournament join(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException {
        Tournament tournament = this.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        if (tournament.hasStarted()) {
            throw new ValidationException("Tournament has already started");
        }
        if (tournament.hasEnded()) {
            throw new ValidationException("Tournament has ended");
        }
        if (tournament.getParticipants().stream().anyMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is already participating in this tournament");
        }

        TournamentParticipant participant = new TournamentParticipant();
        participant.setTournament(tournament);
        participant.setUsername(user.getLogin());
        participant.setScore(0);

        tournament.getParticipants().add(participant);
        return this.save(tournament);
    }

    @Override
    @Transactional
    public Tournament leave(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException {
        Tournament tournament = this.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        if (tournament.hasStarted()) {
            throw new ValidationException("Tournament has already started");
        }
        if (tournament.hasEnded()) {
            throw new ValidationException("Tournament has ended");
        }
        if (tournament.getParticipants().stream().noneMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is not participating in this tournament");
        }

        tournament.getParticipants().removeIf(p -> p.getUsername().equals(user.getLogin()));
        return this.save(tournament);
    }

    @Override
    @Transactional
    public Tournament updateById(Long tournamentId, TournamentDTO tournament, GHUser updater) throws EntityNotFoundException, ValidationException {
        Tournament tournamentToUpdate = this.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        if (!tournamentToUpdate.getCreator().equals(updater.getLogin())) {
            throw new ValidationException("Only the creator of the tournament can update it");
        }
        if (tournamentToUpdate.hasStarted()) {
            throw new ValidationException("Tournament has already started");
        }
        if (tournamentToUpdate.hasEnded()) {
            throw new ValidationException("Tournament has ended");
        }

        tournamentToUpdate.setTitle(tournament.getTitle());
        tournamentToUpdate.setDescription(tournament.getDescription());
        tournamentToUpdate.setStartsAt(tournament.getStartsAt());
        tournamentToUpdate.setEndsAt(tournament.getEndsAt());

        return this.save(tournamentToUpdate);
    }

    @Override
    @Transactional
    public Tournament deleteById(Long tournamentId, GHUser deleter) throws EntityNotFoundException, ValidationException {
        Tournament tournamentToDelete = this.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        if (!tournamentToDelete.getCreator().equals(deleter.getLogin())) {
            throw new ValidationException("Only the creator of the tournament can delete it");
        }
        if (tournamentToDelete.hasStarted()) {
            throw new ValidationException("Tournament has already started");
        }
        if (tournamentToDelete.hasEnded()) {
            throw new ValidationException("Tournament has ended");
        }

        this.tournamentRepository.deleteById(tournamentId);
        return tournamentToDelete;
    }

}
