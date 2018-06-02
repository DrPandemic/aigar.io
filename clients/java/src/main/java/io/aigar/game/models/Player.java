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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalMass() {
        return totalMass;
    }

    public void setTotalMass(int totalMass) {
        this.totalMass = totalMass;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }
}
