package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class GameState {
    private int id;
    private int tick;
    private boolean paused;
    private int multiplier;
    private float timeLeft;
    private List<Player> players;
    private Resources resources;
    private Size map;
    private List<Virus> viruses;
    @JsonIgnore
    private Player me;
    @JsonIgnore
    private List<Player> enemies;

    public int getId() {
        return id;
    }

    public int getTick() {
        return tick;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Resources getResources() {
        return resources;
    }

    public Size getMap() {
        return map;
    }

    public List<Virus> getViruses() {
        return viruses;
    }

    public Player getMe() {
        return me;
    }

    public void setMe(Player me) {
        this.me = me;
    }

    public List<Player> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Player> enemies) {
        this.enemies = enemies;
    }
}
