package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "battle_participants")
@Getter
@Setter
public class BattleParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "battle_id", nullable = false)
    @JsonBackReference
    private Battle battle;

    @Column
    private String username;

    @Column
    private float score;

}
