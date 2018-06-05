package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CellActions {
    @JsonProperty("cell_id")
    private int cellId;
    private boolean burst = false;
    private boolean split = false;
    private int trade = 0;
    private Coordinate target = null;
    @JsonIgnore
    private boolean changed = false;

    public CellActions() {
    }

    public CellActions(int cellId) {
        this.cellId = cellId;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
        changed = true;
    }

    public CellActions withCellId(int cellId) {
        setCellId(cellId);
        return this;
    }

    public boolean isBurst() {
        return burst;
    }

    public void setBurst(boolean burst) {
        this.burst = burst;
        changed = true;
    }

    public CellActions withBurst(boolean burst) {
        setBurst(burst);
        return this;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
        changed = true;
    }

    public CellActions withSplit(boolean split) {
        setSplit(split);
        return this;
    }

    public int getTrade() {
        return trade;
    }

    public void setTrade(int trade) {
        this.trade = trade;
        changed = true;
    }

    public CellActions withTrade(int trade) {
        setTrade(trade);
        return this;
    }

    public Coordinate getTarget() {
        return target;
    }

    public void setTarget(Coordinate target) {
        this.target = target;
        changed = true;
    }

    public CellActions withTarget(Coordinate target) {
        setTarget(target);
        return this;
    }

    @JsonIgnore
    public boolean getChanged() { return changed; }
}
