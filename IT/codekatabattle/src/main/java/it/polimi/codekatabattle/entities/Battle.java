package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

import static it.polimi.codekatabattle.config.APIConstants.DATETIME_FORMAT;

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
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime startsAt;

    @Column
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime endsAt;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<BattleParticipant> participants;

}
