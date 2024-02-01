package it.polimi.codekatabattle.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class BattleTest implements Serializable {
    private String name;
    private String input;
    private String expectedOutput;
    private int givesScore;
    private boolean isPublic;
}
