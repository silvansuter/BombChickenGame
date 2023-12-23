package com.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;


public class GamePanel extends JPanel {
    private final ArrayList<Entity> entities;
    private final Random random;
    private int score;
    private boolean isGameOver;
    private int timeElapsed = 0;
    private Map<String, Clip> soundClips = new HashMap<>();

    // Initialize and store timers as fields so you can stop them
    Timer spawnTimer = new Timer(1000, e -> spawnEntity());
    Timer refreshTimer = new Timer(16, e -> repaint());
    Timer updateEntitiesTimer = new Timer(16, e -> updateEntities());

    public GamePanel() {
        entities = new ArrayList<>();
        random = new Random();
        score = 0;
        isGameOver = false;

        loadSounds();

        setPreferredSize(new Dimension(800, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //System.out.println("Mouse Pressed at: [" + e.getX() + ", " + e.getY() + "]");
                checkEntityClicked(e.getX(), e.getY());
            }
        });

        requestFocusInWindow();

        // Timer to spawn points
        scheduleNextSpawn();

        // Timer for game refresh
        refreshTimer.start();

        updateEntitiesTimer.start();
    }

    private void scheduleNextSpawn() {
        int delay = random.nextInt(500, 2000)/speedup(); // Random delay between 500ms to 2000ms
        spawnTimer = new Timer(delay, e -> spawnEntity());
        spawnTimer.setRepeats(false); // Ensure the timer only triggers once per scheduling
        spawnTimer.start();
    }

    private void spawnEntity() {
        int pointDiameter = 30;
        int x = random.nextInt(getWidth() - pointDiameter);
        int y = random.nextInt(getHeight() - pointDiameter);
        int timeTillDie = random.nextInt(100, 500)/speedup();
        int determineType = random.nextInt(100);
        if (determineType <= 80) {
            entities.add(new Bomb(x, y, timeTillDie));
            playSound("BombComing.wav");
        } else {
            int speedX = random.nextInt(5*speedup());
            int speedY = random.nextInt(5*speedup());
            entities.add(new Chicken(x, y, 3*timeTillDie, speedX, speedY));
            playSound("ChickenSound.wav");
        }
        scheduleNextSpawn();
    }
    
    private void updateEntities() {
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            entity.decrementTimeTillDie();
            if (entity instanceof Bomb && entity.isTimeUp()) {
                playSound("BombDetonating.wav");
                gameOver("Bomb exploded!");
                return;
            }
            else if (entity instanceof Chicken) {
                Chicken chickenEntity = (Chicken) entity;
                chickenEntity.updatePosition();
                if (chickenEntity.isTimeUp()) {
                    iterator.remove();
                }
            }
        }
        timeElapsed++;
        repaint();
    }
    
    private void checkEntityClicked(int mouseX, int mouseY) {
        Iterator<Entity> iterator = entities.iterator();

        //System.out.println("Clicked at: " + mouseX + "," + mouseY);

        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (isEntityClicked(entity, mouseX, mouseY)) {
                if (entity instanceof Bomb) {
                    iterator.remove();
                    score++;
                } else if (entity instanceof Chicken) {
                    playSound("ChickenSquashed.wav");
                    gameOver("Squashed a chicken!");
                    return;
                }
                break;
            }
        }
        repaint();
    }

    private boolean isEntityClicked(Entity entity, int mouseX, int mouseY) {
        double pointRadius = 15; // Assuming each entity is drawn as a circle with this radius (half of the diameter)
        boolean inCircle = Math.pow(mouseX - entity.getX() - pointRadius, 2) + Math.pow(mouseY - entity.getY() - pointRadius, 2) <= Math.pow(pointRadius, 2);
        
        //System.out.println("Clicked at " + entity.getX() + "," + entity.getY() + "?" + inCircle);

        return inCircle;
    }
    
    private int speedup() {
        return (int) (Math.log(timeElapsed/1000+2)/(Math.log(2)));
    }
    
    private void gameOver(String message) {
        isGameOver = true;

        // Stop the timers
        spawnTimer.stop();
        refreshTimer.stop();
        updateEntitiesTimer.stop();

        // Show the game over message
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        // Draw your entities with anti-aliasing
        for (Entity entity : entities) {
            g2d.setColor(entity.getColor());
            g2d.fillOval(entity.getX(), entity.getY(), 30, 30);
        }
    
        g2d.setColor(Color.BLACK);
        g2d.drawString("Score: " + score, 10, 50);
    }

    public void playSound(String soundFileName) {
        Clip clip = soundClips.get(soundFileName);
        if (clip == null) {
            System.err.println("Sound not preloaded: " + soundFileName);
            return;
        }
        // If the clip is already playing, stop it and reset it.
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0); // rewind to the beginning
        clip.start();
    }
    
    public void loadSounds() {
        String[] soundNames = { "BombComing.wav", "BombDetonating.wav", "ChickenSound.wav", "ChickenSquashed.wav" };
        for (String soundName : soundNames) {
            try {
                URL soundURL = getClass().getResource("/sounds/" + soundName);
                if (soundURL == null) {
                    throw new IllegalArgumentException("Sound file not found: " + soundName);
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                soundClips.put(soundName, clip);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }
}