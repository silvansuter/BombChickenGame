package com.game;

import java.awt.Color;

public class Bomb extends Entity {

    public Bomb(int x, int y, int timeTillDie) {
        super(x, y, timeTillDie);
    }

    public Color getColor() {
        float ratio = (float) super.getTimeTillDie() / super.getStartTimeTillDie();
        return new Color(1.0f, 0.0f, 0.0f, ratio); // Red color with varying alpha based on time ratio
    }
}
