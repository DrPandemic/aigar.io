package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProtectedData {
    @JsonProperty("player_secret")
    protected String playerSecret;

    public String getPlayerSecret() {
        return playerSecret;
    }

    public void setPlayerSecret(String playerSecret) {
        this.playerSecret = playerSecret;
    }

    public ProtectedData withPlayerSecret(String playerSecret) {
        setPlayerSecret(playerSecret);
        return this;
    }
}
