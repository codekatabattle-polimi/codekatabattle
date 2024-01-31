package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column
    private String repositoryUrl;

    @Column
    private Long repositoryId;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private BattleLanguage language;

    @Column
    private Boolean enableSAT = false;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<BattleParticipant> participants;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<BattleEntry> entries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    @JsonBackReference
    private Tournament tournament;

    public boolean hasStarted() {
        return startsAt.isBefore(LocalDateTime.now());
    }

    public boolean hasEnded() {
        return endsAt.isBefore(LocalDateTime.now());
    }

}
