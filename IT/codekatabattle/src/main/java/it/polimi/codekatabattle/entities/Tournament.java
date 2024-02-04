package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "text")
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
    private List<TournamentParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<TournamentCoordinator> coordinators = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Battle> battles = new ArrayList<>();

    public boolean hasStarted(Clock clock) {
        return startsAt.isBefore(LocalDateTime.now(clock));
    }

    public boolean hasEnded(Clock clock) {
        return endsAt.isBefore(LocalDateTime.now(clock));
    }

}
