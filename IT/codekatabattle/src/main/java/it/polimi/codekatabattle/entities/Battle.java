package it.polimi.codekatabattle.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "battles")
@Getter
@Setter
public class Battle extends BaseEntity {

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

    @OneToMany(mappedBy = "battle")
    private Set<BattleParticipant> participants;

}
