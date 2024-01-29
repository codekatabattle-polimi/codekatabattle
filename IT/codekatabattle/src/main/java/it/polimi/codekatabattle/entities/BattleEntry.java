package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "battle_entries")
@Getter
@Setter
public class BattleEntry extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "battle_id", insertable = false, updatable = false)
    private Battle battle;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private TournamentParticipant participant;

    @Column
    private float score;

}
