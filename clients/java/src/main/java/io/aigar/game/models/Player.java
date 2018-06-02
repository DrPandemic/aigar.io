package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Player {
    private int id;
    private String name;
    @JsonProperty("total_mass")
    private int totalMass;
    private boolean isActive;
    private List<Cell> cells;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalMass() {
        return totalMass;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public List<Cell> getCells() {
        return cells;
    }
}
