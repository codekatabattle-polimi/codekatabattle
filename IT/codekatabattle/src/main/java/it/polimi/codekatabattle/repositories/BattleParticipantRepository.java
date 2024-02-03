package it.polimi.codekatabattle.repositories;

import it.polimi.codekatabattle.entities.BattleParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleParticipantRepository extends JpaRepository<BattleParticipant, Long> {
}
