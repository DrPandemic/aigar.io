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

    public void setId(int id) {
        this.id = id;
        cellActions.setCellId(id);
    }

    public int getMass() {
        return mass;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public Coordinate getTarget() {
        return target;
    }

    public void setTarget(Coordinate target) {
        this.target = target;
    }

    public boolean isBurst() {
        return burst;
    }

    public void setBurst(boolean burst) {
        this.burst = burst;
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
        return cellActions;
    }
}
