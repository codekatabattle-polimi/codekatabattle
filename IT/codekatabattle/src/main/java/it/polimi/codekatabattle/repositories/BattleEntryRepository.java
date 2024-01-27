package it.polimi.codekatabattle.repositories;

import it.polimi.codekatabattle.entities.BattleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleEntryRepository extends JpaRepository<BattleEntry, Long>{
}
