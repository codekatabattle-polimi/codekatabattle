package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.*;
import it.polimi.codekatabattle.models.dto.BattleDTO;
import it.polimi.codekatabattle.models.dto.BattleEntryDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.repositories.BattleEntryRepository;
import it.polimi.codekatabattle.repositories.BattleRepository;
import it.polimi.codekatabattle.services.BattleService;
import it.polimi.codekatabattle.services.ScoreService;
import it.polimi.codekatabattle.services.TournamentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
public class BattleServiceImpl extends CrudServiceImpl<Battle> implements BattleService {

    @Value("${ckb.github.pat}")
    private String githubPAT;

    private final BattleRepository battleRepository;

    private final BattleEntryRepository battleEntryRepository;

    private final TournamentService tournamentService;

    private final ScoreService scoreService;

    public BattleServiceImpl(BattleRepository battleRepository, BattleEntryRepository battleEntryRepository, TournamentService tournamentService, ScoreService scoreService) {
        super(battleRepository);
        this.battleRepository = battleRepository;
        this.battleEntryRepository = battleEntryRepository;
        this.tournamentService = tournamentService;
        this.scoreService = scoreService;
    }

    public Battle create(BattleDTO battle, GHUser creator) throws ValidationException, IOException {
        Tournament tournament = this.tournamentService.findById(battle.getTournamentId())
            .orElseThrow(() -> new ValidationException("Tournament not found by id " + battle.getTournamentId()));

        if (!tournament.getCreator().equals(creator.getLogin()) && tournament.getCoordinators().stream().noneMatch(c -> c.getUsername().equals(creator.getLogin()))) {
            throw new ValidationException("Only the creator of the tournament or one of the coordinators can create battles in it");
        }
        if (battle.getTests().stream().map(BattleTest::getName).distinct().count() != battle.getTests().size()) {
            throw new ValidationException("Two battle tests with the same name are not allowed");
        }

        Battle newBattle = battle.toEntity();
        newBattle.setCreator(creator.getLogin());
        newBattle.setTournament(tournament);
        newBattle = this.battleRepository.save(newBattle);

        GHRepository repository = this.createBattleRepository(newBattle);
        newBattle.setRepositoryId(repository.getId());
        newBattle.setRepositoryUrl(repository.getHtmlUrl().toString());

        return this.battleRepository.save(newBattle);
    }

    @Override
    @Transactional
    public Battle join(Long battleId, GHUser user) throws EntityNotFoundException, ValidationException {
        Battle battle = this.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        if (battle.getTournament().getParticipants().stream().noneMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is not participating in this tournament");
        }
        if (battle.getTournament().hasEnded()) {
            throw new ValidationException("Tournament has ended, can't join battles");
        }
        if (!battle.hasStarted()) {
            throw new ValidationException("Tournament has not started yet, it is not possible to join");
        }
        if (battle.hasEnded()) {
            throw new ValidationException("Tournament has ended");
        }
        if (battle.getParticipants().stream().anyMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is already participating in this tournament");
        }

        BattleParticipant participant = new BattleParticipant();
        participant.setBattle(battle);
        participant.setUsername(user.getLogin());
        participant.setScore(0);

        battle.getParticipants().add(participant);
        return this.save(battle);
    }

    @Override
    @Transactional
    public Battle leave(Long battleId, GHUser user) throws EntityNotFoundException, ValidationException {
        Battle battle = this.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        BattleParticipant participant = battle.getParticipants().stream()
            .filter(p -> p.getUsername().equals(user.getLogin()))
            .findFirst()
            .orElse(null);

        if (participant == null) {
            throw new ValidationException("User is not participating in this battle");
        }
        if (battle.hasEnded()) {
            throw new ValidationException("Battle has ended");
        }

        battle.getParticipants().remove(participant);
        return this.save(battle);
    }

    @Override
    @Transactional
    public Battle updateById(Long battleId, BattleDTO battle, GHUser updater) throws EntityNotFoundException, ValidationException {
        Battle battleToUpdate = this.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        if (!battleToUpdate.getCreator().equals(updater.getLogin())) {
            throw new ValidationException("Only the creator of the battle can update it");
        }
        if (battleToUpdate.hasStarted()) {
            throw new ValidationException("Battle has already started, can't be updated");
        }
        if (battleToUpdate.hasEnded()) {
            throw new ValidationException("Battle has ended, can't be updated");
        }

        battleToUpdate.setTitle(battle.getTitle());
        battleToUpdate.setDescription(battle.getDescription());
        battleToUpdate.setStartsAt(battle.getStartsAt());
        battleToUpdate.setEndsAt(battle.getEndsAt());
        battleToUpdate.setLanguage(battle.getLanguage());

        return this.save(battleToUpdate);
    }

    @Override
    @Transactional
    public Battle deleteById(Long battleId, GHUser deleter) throws EntityNotFoundException, ValidationException, IOException {
        Battle battleToDelete = this.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        if (!battleToDelete.getCreator().equals(deleter.getLogin())) {
            throw new ValidationException("Only the creator of the tournament can delete it");
        }

        this.deleteBattleRepository(battleToDelete);

        this.battleRepository.deleteById(battleId);
        return battleToDelete;
    }

    @Override
    public GHRepository createBattleRepository(Battle battle) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(githubPAT).build();
        GHRepository repository = github.createRepository(battle.getTitle().toLowerCase())
            .owner("codekatabattle-polimi")
            .description(battle.getDescription())
            .private_(false)
            .fromTemplateRepository("codekatabattle-polimi", getKataTemplateFromBattleLanguage(battle.getLanguage()))
            .create();

        try {
            repository.createVariable("API_BASE_URL", "https://api.codekatabattle.orciuolo.it");
            repository.createVariable("BATTLE_ID", battle.getId().toString());
        } catch (Exception e) {
            this.deleteBattleRepository(battle);
            throw e;
        }

        return repository;
    }

    private String getKataTemplateFromBattleLanguage(@NotNull BattleLanguage language) {
        return switch (language) {
            case GOLANG -> "kata-template-golang";
            case PYTHON -> "kata-template-python";
        };
    }

    @Override
    public void deleteBattleRepository(Battle battle) throws IOException {
        if (battle.getRepositoryId() == null) {
            return;
        }

        GitHub github = new GitHubBuilder().withOAuthToken(githubPAT).build();
        GHRepository repository = github.getRepositoryById(battle.getRepositoryId());
        repository.delete();
    }

    @Override
    public BattleEntry submit(Long battleId, BattleEntryDTO battleEntry, GitHub github) throws EntityNotFoundException, ValidationException, IOException {
        Battle battle = this.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        if (!battle.hasStarted()) {
            throw new ValidationException("Battle has not started yet, it is not possible to submit");
        }
        if (battle.hasEnded()) {
            throw new ValidationException("Battle has ended");
        }

        GHMyself submitter = github.getMyself();
        if (submitter == null) {
            throw new ValidationException("User is not authenticated");
        }

        BattleParticipant participant = battle.getParticipants().stream()
            .filter(p -> p.getUsername().equals(submitter.getLogin()))
            .findFirst()
            .orElseThrow(() -> new ValidationException("User is not participating in this battle"));

        BattleEntry be = new BattleEntry();
        be.setBattle(battle);
        be.setParticipant(participant);
        be.setStatus(BattleEntryStatus.QUEUED);
        be.setScore(0);
        this.battleEntryRepository.save(be);

        this.scoreService.processBattleEntry(be, URI.create(battleEntry.getArtifactUrl()).toURL());
        return be;
    }

}
