package it.polimi.codekatabattle.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import it.polimi.codekatabattle.models.BattleEntryProcessResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

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
    private BattleEntryProcessResult processResult;

    @Column
    private Integer score = null;

}
