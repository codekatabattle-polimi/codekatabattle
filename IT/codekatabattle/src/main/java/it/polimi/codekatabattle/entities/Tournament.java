package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
public class Tournament extends BaseEntity {

    @Column
    private String creator;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private LocalDateTime startsAt;

    @Column
    private LocalDateTime endsAt;

    @OneToMany(mappedBy = "tournament")
    private Set<TournamentParticipant> participants;

    public boolean hasStarted() {
        return startsAt.isBefore(LocalDateTime.now());
    }

    public boolean hasEnded() {
        return endsAt.isBefore(LocalDateTime.now());
    }

}
