package it.polimi.codekatabattle.models;

import lombok.Data;

import java.util.List;

@Data
public class SATResult {

    private String satName;

    private List<String> warnings;

    private Integer score;

}
