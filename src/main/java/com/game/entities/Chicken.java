package com.game.entities;

import java.awt.Color;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Chicken extends Entity {
    private int speedX;
    private int speedY;
    private int xLowerEnd = 0;
    private int xHigherEnd = 800;
    private int yLowerEnd = 0;
    private int yHigherEnd = 600;
    private BufferedImage chickenImage;

    public Chicken(int x, int y, int timeTillDie, int speedX, int speedY) {
        super(x, y, timeTillDie);
        this.speedX = speedX;
        this.speedY = speedY;

        try {
            // Load the Bomb.png image from a file
            chickenImage = ImageIO.read(getClass().getResource("/images/Chicken.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePosition() {
        
        int oldX = super.getX();
        int oldY = super.getY();

        /*
        super.setX(oldX + speedX);
        super.setY(oldY + speedY);

        return;
        */

        if (speedX > 0) {
            if (oldX + speedX < xHigherEnd) {
                super.setX(oldX + speedX);
            }
            else {
                super.setX(xHigherEnd - (speedX - (xHigherEnd - oldX)));
                this.speedX = -speedX;
            }
        }
        else {
            if (oldX + speedX > xLowerEnd) {
                super.setX(oldX + speedX);
            }
            else {
                super.setX(xLowerEnd + (speedX - (oldX - xLowerEnd)));
                this.speedX = -speedX;
            }
        }

        if (speedY > 0) {
            if (oldY + speedY < yHigherEnd) {
                super.setY(oldY + speedY);
            }
            else {
                super.setY(yHigherEnd - (speedY - (yHigherEnd - oldY)));
                this.speedY = -speedY;
            }
        }
        else {
            if (oldY + speedY > yLowerEnd) {
                super.setY(oldY + speedY);
            }
            else {
                super.setY(yLowerEnd + (speedY - (oldY - yLowerEnd)));
                this.speedY = -speedY;
            }
        }
    }

    public Color getColor() {
        return Color.BLUE;
    }

    public BufferedImage getImage() {
        if (speedX < 0) {
            // Create an AffineTransform for horizontal mirroring
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-chickenImage.getWidth(null), 0);

            // Create an AffineTransformOp and apply the transformation to the image
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            BufferedImage mirroredImage = op.filter(chickenImage, null);

            return mirroredImage;
        }
        return chickenImage;
    }
}
