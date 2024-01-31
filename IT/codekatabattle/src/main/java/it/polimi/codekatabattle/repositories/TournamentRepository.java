package it.polimi.codekatabattle.repositories;

import it.polimi.codekatabattle.entities.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("SELECT t FROM Tournament t WHERE t.creator = ?1")
    Page<Tournament> findAllByCreator(Pageable pageable, String creator);

    @Query("SELECT t FROM Tournament t WHERE ?1 MEMBER OF t.participants")
    Page<Tournament> findAllByParticipant(Pageable pageable, String participant);

    @Query("SELECT t FROM Tournament t WHERE ?1 MEMBER OF t.coordinators")
    Page<Tournament> findAllByCoordinator(Pageable pageable, String coordinator);

}
