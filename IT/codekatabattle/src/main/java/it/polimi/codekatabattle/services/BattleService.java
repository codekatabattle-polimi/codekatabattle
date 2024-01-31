package it.polimi.codekatabattle.services;

import it.polimi.codekatabattle.entities.Battle;
import it.polimi.codekatabattle.entities.BattleEntry;
import it.polimi.codekatabattle.models.dto.BattleDTO;
import it.polimi.codekatabattle.models.dto.BattleEntryDTO;
import it.polimi.codekatabattle.models.github.GHUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

public interface BattleService extends CrudService<Battle> {

    Battle create(BattleDTO battle, GHUser creator) throws ValidationException, IOException;

    Battle join(Long battleId, GHUser user) throws EntityNotFoundException, ValidationException;

    Battle leave(Long battleId, GHUser user) throws EntityNotFoundException, ValidationException;

    Battle updateById(Long battleId, BattleDTO battle, GHUser updater) throws EntityNotFoundException, ValidationException;

    Battle deleteById(Long battleId, GHUser deleter) throws EntityNotFoundException, ValidationException, IOException;

    GHRepository createBattleRepository(Battle battle) throws IOException;

    void deleteBattleRepository(Battle battle) throws IOException;

    BattleEntry submit(Long battleId, BattleEntryDTO battleEntry, GitHub github) throws EntityNotFoundException, ValidationException, IOException;
}
