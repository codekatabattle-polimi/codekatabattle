package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "participants")
@Getter
@Setter
public class TournamentParticipant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "participant_id", insertable = false, updatable = false)
    private Tournament tournament;

    @Column
    private String username;

    @Column
    private float score;

}
