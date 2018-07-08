package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mini2Dx.gdx.math.Vector2;

import java.util.Optional;

public class Cell {
    private int id;
    private int mass;
    private int radius;
    private Vector2 position;
    private Vector2 target;
    private boolean burst;
    @JsonIgnore
    private CellActions cellActions;

    public int getId() {
        return id;
    }

    public int getMass() {
        return mass;
    }

    public int getRadius() {
        return radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getTarget() {
        return target;
    }

    public boolean isBurst() {
        return burst;
    }

    @JsonIgnore
    public void move(Vector2 target) {
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
    public void initCellActions() {
        cellActions = new CellActions();
    }

    @JsonIgnore
    public Optional<CellActions> getActions() {
        if(!cellActions.getChanged())
            return Optional.empty();

        if(cellActions.getTarget() == null)
            cellActions.setTarget(target);

        return Optional.of(cellActions.withCellId(id));
    }
}
