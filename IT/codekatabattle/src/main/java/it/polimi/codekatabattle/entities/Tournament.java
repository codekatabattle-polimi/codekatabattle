package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

import static it.polimi.codekatabattle.config.APIConstants.DATETIME_FORMAT;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
public class Tournament extends BaseEntity {

    @Column
    @NotNull
    private String creator;

    @Column
    @NotNull
    private String title;

    @Column
    private String description;

    @Column
    @NotNull
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime startsAt;

    @Column
    @NotNull
    @JsonFormat(pattern=DATETIME_FORMAT)
    private LocalDateTime endsAt;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private TournamentPrivacy privacy = TournamentPrivacy.PUBLIC;

    @Column
    private Integer maxParticipants;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<TournamentParticipant> participants;

    public boolean hasStarted() {
        return startsAt.isBefore(LocalDateTime.now());
    }

    public boolean hasEnded() {
        return endsAt.isBefore(LocalDateTime.now());
    }

}
