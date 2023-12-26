package com.game.entities;

import java.awt.Color;

import java.awt.image.BufferedImage;

public abstract class Entity {
    private int x;
    private int y;
    private int startTimeTillDie;
    private int timeTillDie;

    public Entity(int x, int y, int timeTillDie) {
        this.x = x;
        this.y = y;
        this.startTimeTillDie = timeTillDie;
        this.timeTillDie = timeTillDie;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getTimeTillDie() { return timeTillDie; }
    public int getStartTimeTillDie() { return startTimeTillDie; }

    public void decrementTimeTillDie() {
        if (timeTillDie > 0) {
            timeTillDie--;
        }
    }

    public float getTimeAliveFraction() {
        return (float) this.timeTillDie / this.startTimeTillDie;
    }

    public boolean isTimeUp() {
        return timeTillDie <= 0;
    }

    public abstract Color getColor();

    public abstract BufferedImage getImage();
}
