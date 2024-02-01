package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "battle_entries")
@Getter
@Setter
public class BattleEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "battle_id", nullable = false)
    @JsonBackReference
    private Battle battle;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "battle_participant_id")
    private BattleParticipant participant;

    @Column
    @Enumerated(EnumType.STRING)
    private BattleEntryStatus status = BattleEntryStatus.QUEUED;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private List<BattleTestResult> testResults = new ArrayList<>();

    @Column
    private Integer score = null;

}
