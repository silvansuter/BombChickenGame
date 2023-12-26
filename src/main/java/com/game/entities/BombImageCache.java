package com.game.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BombImageCache {
    private static final int IMAGE_COUNT = 100;
    private static final List<BufferedImage> imageCache = new ArrayList<>(IMAGE_COUNT);

    static {
        BufferedImage bombImage = null;
        try {
            // Load the Bomb.png image from a file
            bombImage = ImageIO.read(BombImageCache.class.getResource("/images/Bomb.png"));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, e.g., exit the program or use a default image
        }

        for (int i = 0; i < IMAGE_COUNT; i++) {
            float ratio = i / (float) IMAGE_COUNT;
            BufferedImage processedImage = processImage(bombImage, ratio);
            imageCache.add(processedImage);
        }
    }

    public static int getImageCount() {
        return IMAGE_COUNT;
    }

    public static void init() {
    }

    private static BufferedImage processImage(BufferedImage sourceImage, float ratio) {
        // Clone the original image to avoid modifying it directly
        BufferedImage redderImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = redderImage.createGraphics();
        // Draw the original image
        g2d.drawImage(sourceImage, 0, 0, null);
        g2d.dispose();

        // Apply a red color filter based on the ratio
        for (int x = 0; x < redderImage.getWidth(); x++) {
            for (int y = 0; y < redderImage.getHeight(); y++) {
                int rgba = redderImage.getRGB(x, y);
                int red = (rgba >> 16) & 0xFF;
                int green = (rgba >> 8) & 0xFF;
                int blue = rgba & 0xFF;
                int alpha = (rgba >> 24) & 0xFF;

                // Increase the red component based on the ratio
                red = Math.min(255, (int)(red + (255 - red) * (1 - ratio)));
                
                // Recombine the components and update the pixel
                rgba = (alpha << 24) | (red << 16) | (green << 8) | blue;
                redderImage.setRGB(x, y, rgba);
            }
        }

        return redderImage;
    }

    public static BufferedImage getImage(int index) {
        if (index >= 0 && index < IMAGE_COUNT) {
            return imageCache.get(index);
        } else {
            throw new IllegalArgumentException("Index out of range");
        }
    }
}
