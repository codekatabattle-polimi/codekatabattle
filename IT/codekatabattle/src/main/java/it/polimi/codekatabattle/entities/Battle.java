package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import it.polimi.codekatabattle.models.BattleTest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column
    private Boolean enableManualEvaluation = false;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private List<BattleTest> tests = new ArrayList<>();

    @Column
    private int timelinessBaseScore = 0;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<BattleParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<BattleEntry> entries = new ArrayList<>();

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
