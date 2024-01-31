package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "battle_entries")
@Getter
@Setter
public class BattleEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "battle_id", nullable = false)
    @JsonBackReference
    private Battle battle;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "battle_participant_id")
    private BattleParticipant participant;

    @Column
    private float score;

}
