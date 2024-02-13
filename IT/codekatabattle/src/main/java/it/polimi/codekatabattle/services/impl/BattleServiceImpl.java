package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.*;
import it.polimi.codekatabattle.models.BattleTest;
import it.polimi.codekatabattle.models.dto.BattleDTO;
import it.polimi.codekatabattle.models.dto.BattleEntryDTO;
import it.polimi.codekatabattle.models.dto.BattleParticipantUpdateDTO;
import it.polimi.codekatabattle.models.dto.BattleUpdateDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import it.polimi.codekatabattle.repositories.BattleEntryRepository;
import it.polimi.codekatabattle.repositories.BattleParticipantRepository;
import it.polimi.codekatabattle.repositories.BattleRepository;
import it.polimi.codekatabattle.services.BattleService;
import it.polimi.codekatabattle.services.ScoreService;
import it.polimi.codekatabattle.services.TournamentService;
import it.polimi.codekatabattle.utils.clock.ConfigurableClock;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
public class BattleServiceImpl implements BattleService {

    @Value("${ckb.github.pat}")
    private String githubPAT;

    private final BattleRepository battleRepository;

    private final BattleParticipantRepository battleParticipantRepository;

    private final BattleEntryRepository battleEntryRepository;

    private final TournamentService tournamentService;

    private final ScoreService scoreService;

    private final ConfigurableClock clock;

    public BattleServiceImpl(
        BattleRepository battleRepository,
        BattleParticipantRepository battleParticipantRepository,
        BattleEntryRepository battleEntryRepository,
        TournamentService tournamentService,
        ScoreService scoreService,
        ConfigurableClock clock
    ) {
        this.battleRepository = battleRepository;
        this.battleParticipantRepository = battleParticipantRepository;
        this.battleEntryRepository = battleEntryRepository;
        this.tournamentService = tournamentService;
        this.scoreService = scoreService;
        this.clock = clock;
    }

    public Battle create(BattleDTO battle, GHUser creator) throws ValidationException, EntityNotFoundException, IOException {
        Tournament tournament = this.tournamentService.findById(battle.getTournamentId());

        if (!tournament.getCreator().equals(creator.getLogin()) && tournament.getCoordinators().stream().noneMatch(c -> c.getUsername().equals(creator.getLogin()))) {
            throw new ValidationException("Only the creator of the tournament or one of the coordinators can create battles in it");
        }
        if (battle.getTests().stream().map(BattleTest::getName).distinct().count() != battle.getTests().size()) {
            throw new ValidationException("Two battle tests with the same name are not allowed");
        }
        if (battle.getEndsAt().isBefore(battle.getStartsAt())) {
            throw new ValidationException("The enrollment deadline must be before the final deadline");
        }
        if (battle.getEndsAt().isAfter(tournament.getEndsAt())) {
            throw new ValidationException("The battle final deadline must be before the tournament final deadline");
        }
        if (battle.getStartsAt().isBefore(tournament.getStartsAt())) {
            throw new ValidationException("The battle enrollament deadline must be after the tournament enrollment deadline");
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

    public Battle findById(Long battleId) throws EntityNotFoundException {
        Battle battle = this.battleRepository.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        // Include only public tests
        battle.setTests(battle.getTests().stream().filter(test -> test.getPrivacy() == BattleTestPrivacy.PUBLIC).toList());

        return battle;
    }

    @Override
    public Page<Battle> findAll(Pageable pageable) {
        // Include only public tests
        return this.battleRepository.findAll(pageable).map(battle -> {
            battle.setTests(battle.getTests().stream().filter(test -> test.getPrivacy() == BattleTestPrivacy.PUBLIC).toList());
            return battle;
        });
    }

    @Override
    @Transactional
    public Battle join(Long battleId, GHUser user) throws EntityNotFoundException, ValidationException {
        Battle battle = this.battleRepository.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        if (battle.getTournament().getParticipants().stream().noneMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is not participating in this tournament");
        }
        if (battle.getTournament().hasEnded(clock.getClock())) {
            throw new ValidationException("Tournament has ended, can't join battles");
        }

        if (!battle.canStudentEnroll(clock.getClock())) {
            throw new ValidationException("Battle enrollment deadline has expired");
        }
        if (battle.hasEnded(clock.getClock())) {
            throw new ValidationException("Battle has ended");
        }
        if (battle.getParticipants().stream().anyMatch(p -> p.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("User is already participating in this battle");
        }

        // These are mostly paranoid checks, as the first check already prevents creator/coordinators from joining
        if (battle.getTournament().getCreator().equals(user.getLogin())) {
            throw new ValidationException("Tournament creator cannot join its battles");
        }
        if (battle.getTournament().getCoordinators().stream().anyMatch(c -> c.getUsername().equals(user.getLogin()))) {
            throw new ValidationException("Tournament coordinators cannot join its battles");
        }

        BattleParticipant participant = new BattleParticipant();
        participant.setBattle(battle);
        participant.setUsername(user.getLogin());
        participant.setScore(0);

        battle.getParticipants().add(participant);
        return this.battleRepository.save(battle);
    }

    @Override
    @Transactional
    public Battle leave(Long battleId, GHUser user) throws EntityNotFoundException, ValidationException {
        Battle battle = this.battleRepository.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        BattleParticipant participant = battle.getParticipants().stream()
            .filter(p -> p.getUsername().equals(user.getLogin()))
            .findFirst()
            .orElse(null);

        if (participant == null) {
            throw new ValidationException("User is not participating in this battle");
        }
        if (battle.hasEnded(clock.getClock())) {
            throw new ValidationException("Battle has ended");
        }

        battle.getParticipants().remove(participant);
        return this.battleRepository.save(battle);
    }

    @Override
    @Transactional
    public Battle updateById(@NotNull Long battleId, @NotNull BattleUpdateDTO battle, @NotNull GHUser updater) throws EntityNotFoundException, ValidationException {
        Battle battleToUpdate = this.battleRepository.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        Tournament tournament = battleToUpdate.getTournament();

        if (!battleToUpdate.getCreator().equals(updater.getLogin())) {
            throw new ValidationException("Only the creator of the battle can update it");
        }
        if (battle.getEndsAt().isBefore(battle.getStartsAt())) {
            throw new ValidationException("The enrollament deadline must be before the final deadline");
        }
        if (battle.getEndsAt().isAfter(tournament.getEndsAt())) {
            throw new ValidationException("The battle final deadline must be before the tournament final deadline");
        }
        if (battle.getStartsAt().isBefore(tournament.getStartsAt())) {
            throw new ValidationException("The battle enrollament deadline must be after the tournament enrollment deadline");
        }

        battleToUpdate.setStartsAt(battle.getStartsAt());
        battleToUpdate.setEndsAt(battle.getEndsAt());
        battleToUpdate.setEnableManualEvaluation(battle.getEnableManualEvaluation());

        return this.battleRepository.save(battleToUpdate);
    }

    @Override
    @Transactional
    public Battle updateBattleParticipantById(
        @NotNull Long battleId,
        @NotNull Long battleParticipantId,
        @NotNull BattleParticipantUpdateDTO battleParticipantUpdate,
        @NotNull GHUser updater
    ) throws EntityNotFoundException, ValidationException {
        Battle battleToUpdate = this.battleRepository.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        if (!battleToUpdate.getCreator().equals(updater.getLogin())) {
            throw new ValidationException("Only the creator of the battle can update it");
        }
        if (!battleToUpdate.getEnableManualEvaluation()) {
            throw new ValidationException("Manual evaluation is not enabled for this battle");
        }

        BattleParticipant participantToUpdate = battleToUpdate.getParticipants().stream()
            .filter(p -> p.getId().equals(battleParticipantId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Battle participant not found by id " + battleParticipantId));
        if (participantToUpdate.getReceivedOME()) {
            throw new ValidationException("Participant has already received manual evaluation by the creator of the battle");
        }

        participantToUpdate.setScore(participantToUpdate.getScore() + battleParticipantUpdate.getScore());
        participantToUpdate.setReceivedOME(true);
        this.battleParticipantRepository.save(participantToUpdate);

        return this.battleRepository.save(battleToUpdate);
    }

    @Override
    @Transactional
    public Battle deleteById(Long battleId, GHUser deleter) throws EntityNotFoundException, ValidationException, IOException {
        Battle battleToDelete = this.battleRepository.findById(battleId)
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
            repository.createVariable("API_BASE_URL", "https://codekatabattle-api.onrender.com.");
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
    @Transactional
    public BattleEntry submit(Long battleId, BattleEntryDTO battleEntry, GitHub github) throws EntityNotFoundException, ValidationException, IOException {
        Battle battle = this.battleRepository.findById(battleId)
            .orElseThrow(() -> new EntityNotFoundException("Battle not found by id " + battleId));

        if (!battle.canStudentEnroll(clock.getClock())) {
            throw new ValidationException("Battle enrollment deadline has expired");
        }
        if (battle.hasEnded(clock.getClock())) {
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
        this.battleEntryRepository.save(be);

        this.scoreService.processBattleEntry(be, URI.create(battleEntry.getArtifactUrl()).toURL());
        return be;
    }

}
