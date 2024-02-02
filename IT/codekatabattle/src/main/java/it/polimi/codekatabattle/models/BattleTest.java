package it.polimi.codekatabattle.models;

import it.polimi.codekatabattle.entities.BattleTestPrivacy;
import lombok.Data;

import java.io.Serializable;

@Data
public class BattleTest implements Serializable {
    private String name;
    private String input;
    private String expectedOutput;
    private int givesScore;
    private BattleTestPrivacy privacy;
}
