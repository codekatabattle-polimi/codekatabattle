package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "battles")
@Getter
@Setter
public class Battle extends Tournament {

    @OneToMany(mappedBy = "battle")
    private Set<BattleEntry> entries;

    @Column
    private String repositoryUrl;

}
