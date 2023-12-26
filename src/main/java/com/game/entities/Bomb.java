package com.game.entities;

import java.awt.Color;
import java.util.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bomb extends Entity {

    public Bomb(int x, int y, int timeTillDie) {
        super(x, y, timeTillDie);
    }

    public Color getColor() {
        float ratio = getTimeAliveFraction();
        return new Color(1.0f, 0.0f, 0.0f, ratio); // Red color with varying alpha based on time ratio
    }

    public BufferedImage getImage() {
        float ratio = getTimeAliveFraction();
        int index = (int) (ratio * (BombImageCache.getImageCount() - 1)); // Calculate index based on ratio
        return BombImageCache.getImage(index);
    }
}
