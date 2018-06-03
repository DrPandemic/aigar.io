package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cell {
    private int id;
    private int mass;
    private int radius;
    private Coordinate position;
    private Coordinate target;
    private boolean burst;
    @JsonIgnore
    private CellActions cellActions = new CellActions();

    public int getId() {
        return id;
    }

    public int getMass() {
        return mass;
    }

    public int getRadius() {
        return radius;
    }

    public Coordinate getPosition() {
        return position;
    }

    public Coordinate getTarget() {
        return target;
    }

    public boolean isBurst() {
        return burst;
    }

    @JsonIgnore
    public void move(Coordinate target) {
        cellActions.setTarget(target);
    }

    @JsonIgnore
    public void split() {
        cellActions.setSplit(true);
    }

    @JsonIgnore
    public void burst() {
        cellActions.setBurst(true);
    }

    @JsonIgnore
    public void trade(int quantity) {
        cellActions.setTrade(quantity);
    }

    @JsonIgnore
    public CellActions getActions() {
        return cellActions.withCellId(id);
    }
}
