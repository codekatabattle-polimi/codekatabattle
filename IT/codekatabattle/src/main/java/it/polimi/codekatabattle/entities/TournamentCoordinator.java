package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tournament_coordinators")
@Getter
@Setter
public class TournamentCoordinator extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    @JsonBackReference
    private Tournament tournament;

    @Column
    private String username;

}
