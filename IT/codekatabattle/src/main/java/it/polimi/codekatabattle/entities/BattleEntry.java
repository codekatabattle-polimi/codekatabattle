package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "battle_entries")
@Getter
@Setter
public class BattleEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_id", nullable = false)
    private Battle battle;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "battle_participant_id")
    private BattleParticipant participant;

    @Column
    private float score;

}
