package io.aigar.game.models;

import org.mini2Dx.gdx.math.Vector2;

public class Virus {
    private int radius;
    private int mass;
    private Vector2 position;

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMass() {
        return mass;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
