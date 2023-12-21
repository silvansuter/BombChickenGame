package com.game;

import java.awt.Color;

public class Entity {
    private int x;
    private int y;
    private String type;
    private int startTimeTillDie;
    private int timeTillDie;

    public Entity(int x, int y, String type, int timeTillDie) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.startTimeTillDie = timeTillDie;
        this.timeTillDie = timeTillDie;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getType() { return type; }

    public void decrementTimeTillDie() {
        if (timeTillDie > 0) {
            timeTillDie--;
        }
    }

    public boolean isTimeUp() {
        return timeTillDie <= 0;
    }

    public boolean isBomb() {
        return "bomb".equals(type);
    }

    public boolean isChicken() {
        return "chicken".equals(type);
    }

    public Color getColor() {
        if ("chicken".equals(type)) {
            return Color.BLUE;
        } else if ("bomb".equals(type)) {
            float ratio = (float) timeTillDie / startTimeTillDie;
            return new Color(1.0f, 0.0f, 0.0f, ratio); // Red color with varying alpha based on time ratio
        }
        return Color.BLACK; // Default color, in case of an unknown type
    }
}
