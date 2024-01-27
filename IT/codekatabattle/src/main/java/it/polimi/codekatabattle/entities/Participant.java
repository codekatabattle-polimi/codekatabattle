package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "participants")
@Getter
@Setter
public class Participant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "participant_id", insertable = false, updatable = false)
    private Tournament tournament;

    @Column
    private String username;

    @Column
    @Enumerated(EnumType.STRING)
    private ParticipantType type;

    @Column
    private float score;

}
