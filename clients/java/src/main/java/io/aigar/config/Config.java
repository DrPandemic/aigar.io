package io.aigar.config;

public class Config {
    private int playerId;
    private String playerSecret;
    private String apiUrl;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerSecret() {
        return playerSecret;
    }

    public void setPlayerSecret(String playerSecret) {
        this.playerSecret = playerSecret;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
