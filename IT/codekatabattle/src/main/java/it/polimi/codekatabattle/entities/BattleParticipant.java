package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "battle_participants")
@Getter
@Setter
public class BattleParticipant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "participant_id", insertable = false, updatable = false)
    private Battle battle;

    @Column
    private String username;

    @Column
    private float score;

}
