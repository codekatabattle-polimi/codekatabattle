package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.TournamentCoordinator;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament create(@Valid @NotNull TournamentDTO tournament, @NotNull GHUser creator) throws ValidationException {
        if (tournament.getEndsAt().isBefore(tournament.getStartsAt())) {
            throw new ValidationException("The enrollament deadline must be before the final deadline");
        }
        Tournament entity = tournament.toEntity();
        entity.setCreator(creator.getLogin());
        return this.tournamentRepository.save(entity);
    }

    public Tournament findById(Long tournamentId) throws EntityNotFoundException {
        return this.tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));
    }

    @Override
    public Page<Tournament> findAllPublic(Pageable pageable) {
        return this.tournamentRepository.findAllPublic(pageable);
    }

    @Override
    public Page<Tournament> findAllByCreator(Pageable pageable, String creator) {
        return this.tournamentRepository.findAllByCreator(pageable, creator);
    }

    @Override
    public Page<Tournament> findAllByParticipant(Pageable pageable, String participant) {
        return this.tournamentRepository.findAllByParticipant(pageable, participant);
    }

    @Override
    public Page<Tournament> findAllByCoordinator(Pageable pageable, String coordinator) {
        return this.tournamentRepository.findAllByCoordinator(pageable, coordinator);
    }

    @Override
    @Transactional
    public Tournament join(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException {
        Tournament tournament = this.tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        if (tournament.hasStarted()) {
            throw new ValidationException("Tournament has already started, it is not possible to join");
        }
        if (tournament.hasEnded()) {
            throw new ValidationException("Tournament has ended");
        }
        if (tournament.getMaxParticipants() != null && tournament.getParticipants().size() + 1 > tournament.getMaxParticipants()) {
            throw new ValidationException("Tournament is full");
        }
        if (tournament.getParticipants().stream().anyMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is already participating in this tournament");
        }
        if (tournament.getCreator().equals(user.getLogin())) {
            throw new ValidationException("Tournament creator can't join it");
        }

        TournamentParticipant participant = new TournamentParticipant();
        participant.setTournament(tournament);
        participant.setUsername(user.getLogin());
        participant.setScore(0);

        tournament.getParticipants().add(participant);
        return this.tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public Tournament leave(Long tournamentId, GHUser user) throws EntityNotFoundException, ValidationException {
        Tournament tournament = this.tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        TournamentParticipant participant = tournament.getParticipants().stream()
            .filter(p -> p.getUsername().equals(user.getLogin()))
            .findFirst()
            .orElse(null);

        if (tournament.getParticipants().stream().noneMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is not participating in this tournament");
        }
        if (tournament.hasEnded()) {
            throw new ValidationException("Tournament has ended");
        }

        tournament.getParticipants().remove(participant);
        return this.tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public Tournament updateById(Long tournamentId, TournamentDTO tournament, GHUser updater) throws EntityNotFoundException, ValidationException {
        Tournament tournamentToUpdate = this.tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        if (!tournamentToUpdate.getCreator().equals(updater.getLogin())) {
            throw new ValidationException("Only the creator of the tournament can update it");
        }
        if (tournamentToUpdate.hasStarted()) {
            throw new ValidationException("Tournament has already started, can't be updated");
        }
        if (tournamentToUpdate.hasEnded()) {
            throw new ValidationException("Tournament has ended, can't be updated");
        }

        if (tournamentToUpdate.getMaxParticipants() != null && tournamentToUpdate.getParticipants().size() > tournamentToUpdate.getMaxParticipants()) {
            throw new ValidationException("Tournament maximum number of participants can't be lower than the current number of participants");
        }

        tournamentToUpdate.setTitle(tournament.getTitle());
        tournamentToUpdate.setDescription(tournament.getDescription());
        tournamentToUpdate.setStartsAt(tournament.getStartsAt());
        tournamentToUpdate.setEndsAt(tournament.getEndsAt());
        tournamentToUpdate.setPrivacy(tournament.getPrivacy());
        tournamentToUpdate.setMaxParticipants(tournament.getMaxParticipants());

        if (tournament.getCoordinators().size() > 0) {
            tournamentToUpdate.setCoordinators(tournament.getCoordinators().stream().map(c -> {
                TournamentCoordinator tc = new TournamentCoordinator();
                tc.setUsername(c);
                tc.setTournament(tournamentToUpdate);
                return tc;
            }).toList());
        }

        return this.tournamentRepository.save(tournamentToUpdate);
    }

    @Override
    @Transactional
    public Tournament deleteById(Long tournamentId, GHUser deleter) throws EntityNotFoundException, ValidationException {
        Tournament tournamentToDelete = this.tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException("Tournament not found by id " + tournamentId));

        if (!tournamentToDelete.getCreator().equals(deleter.getLogin())) {
            throw new ValidationException("Only the creator of the tournament can delete it");
        }

        this.tournamentRepository.deleteById(tournamentId);
        return tournamentToDelete;
    }

}
