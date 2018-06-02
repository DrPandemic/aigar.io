package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class GameState {
    private int id;
    private int tick;
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

    public void setId(int id) {
        this.id = id;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(float timeLeft) {
        this.timeLeft = timeLeft;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Size getMap() {
        return map;
    }

    public void setMap(Size map) {
        this.map = map;
    }

    public List<Virus> getViruses() {
        return viruses;
    }

    public void setViruses(List<Virus> viruses) {
        this.viruses = viruses;
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
