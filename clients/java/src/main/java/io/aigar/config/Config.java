package io.aigar.config;

public class Config {
    private int playerId;
    private String playerSecret;
    private String apiUrl;

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerSecret() {
        return playerSecret;
    }

    public String getApiUrl() {
        if(apiUrl.charAt(apiUrl.length() - 1) != '/') {
            return apiUrl.concat("/");
        }

        return apiUrl;
    }
}
