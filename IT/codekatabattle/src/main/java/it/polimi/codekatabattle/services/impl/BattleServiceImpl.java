package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.entities.Battle;
import it.polimi.codekatabattle.repositories.BattleRepository;
import it.polimi.codekatabattle.services.BattleService;

public class BattleServiceImpl extends CrudServiceImpl<Battle> implements BattleService {

    private final BattleRepository battleRepository;

    public BattleServiceImpl(BattleRepository battleRepository) {
        super(battleRepository);
        this.battleRepository = battleRepository;
    }

}
