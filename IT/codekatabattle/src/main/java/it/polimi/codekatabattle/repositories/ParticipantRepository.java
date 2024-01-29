package it.polimi.codekatabattle.repositories;

import it.polimi.codekatabattle.entities.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<TournamentParticipant, Long> {
}
