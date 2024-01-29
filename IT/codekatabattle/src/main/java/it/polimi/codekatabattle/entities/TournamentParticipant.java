package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tournament_participants")
@Getter
@Setter
public class TournamentParticipant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "tournament_id", insertable = false, updatable = false)
    private Tournament tournament;

    @Column
    private String username;

    @Column
    private float score;

}
