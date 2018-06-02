package io.aigar.game.models;

import java.util.List;

public class Action extends ProtectedData {
    private List<CellActions> actions;

    public Action(String playerSecret, List<CellActions> actions) {
        this.playerSecret = playerSecret;
        this.actions = actions;
    }

    public List<CellActions> getActions() {
        return actions;
    }

    public void setActions(List<CellActions> actions) {
        this.actions = actions;
    }
}
