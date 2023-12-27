package com.game.entities;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import java.lang.Math;

import java.util.function.Consumer;

public class EntityManager {

    private ArrayList<Entity> entities;

    private Consumer<String> onPlaySound;
    private Consumer<String> onGameOver;
    private Runnable onRefreshPanel;
    private Runnable onScoreUpdate;

    private int panelWidth;
    private int panelHeight;

    private double speedup;

    private Timer updateEntitiesTimer;
    private Timer spawnTimer;
    private final Random random;

    public EntityManager(Consumer<String> onPlaySound, Consumer<String> onGameOver, Runnable onRefreshPanel, Runnable onScoreUpdate, int panelWidth, int panelHeight) {
        this.onPlaySound = onPlaySound;
        this.onGameOver = onGameOver;
        this.onRefreshPanel = onRefreshPanel;
        this.onScoreUpdate = onScoreUpdate;

        this.random = new Random();

        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    public void startGame() {
        entities = new ArrayList<>();

        this.updateEntitiesTimer = new Timer(16, e -> updateEntities());
        this.spawnTimer = new Timer(1000, e -> spawnEntity());
        spawnTimer.setRepeats(false);

        updateEntitiesTimer.start();
        spawnTimer.start();        
    }

    public void endGame() {
        updateEntitiesTimer.stop();
        spawnTimer.stop();  // Stop the spawn timer
        spawnTimer = null;  // Nullify the timer to avoid rescheduling
    }
    
    private void updateEntities() {
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            entity.decrementTimeTillDie();
            if (entity instanceof Bomb && entity.isTimeUp()) {
                onPlaySound.accept("BombDetonating.wav");
                onGameOver.accept("Bomb exploded!");
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
        onRefreshPanel.run();
    }

    private void scheduleNextSpawn() {
        if (spawnTimer == null) {
            return; // Don't schedule if the game has ended
        }
    
        int delay = (int) Math.round(random.nextInt(1500, 2000) / speedup); // Random delay between 500ms to 2000ms
        spawnTimer = new Timer(delay, e -> spawnEntity());
        spawnTimer.setRepeats(false); // Ensure the timer only triggers once per scheduling
        spawnTimer.start();
    }

    public void setSpeedupFactor(double speedup) {
        this.speedup = speedup;
    }

    private void spawnEntity() {
        int pointDiameter = 30;
        int x = random.nextInt(panelWidth - pointDiameter);
        int y = random.nextInt(panelHeight - pointDiameter);
        int timeTillDie = (int) Math.round(random.nextInt(400, 500)/speedup);
        int determineType = random.nextInt(100);
        if (determineType <= 80) {
            entities.add(new Bomb(x, y, timeTillDie));
            onPlaySound.accept("BombComing.wav");
        } else {
            int speedX = (int) Math.round(random.nextDouble(5*speedup));
            int speedY;
            // Do not allow the chicken to be stationary
            if (speedX == 0) {
                speedY = (int) Math.round(random.nextDouble(1, 5*speedup));
            }
            else {
                speedY = (int) Math.round(random.nextDouble(5*speedup));
            }
            entities.add(new Chicken(x, y, 3*timeTillDie, speedX, speedY));
            onPlaySound.accept("ChickenSound.wav");
        }
        scheduleNextSpawn();
    }

    public void checkEntityClicked(int mouseX, int mouseY) {
        Iterator<Entity> iterator = entities.iterator();

        //System.out.println("Clicked at: " + mouseX + "," + mouseY);

        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (isMouseOnEntity(entity, mouseX, mouseY)) {
                if (entity instanceof Bomb) {
                    iterator.remove();
                    onScoreUpdate.run();
                }
                else if (entity instanceof Chicken) {
                    onPlaySound.accept("ChickenSquashed.wav");
                    onGameOver.accept("Squashed a chicken!");
                    return;
                }
                break;
            }
        }
    }

    public boolean isMouseOverChicken(int mouseX, int mouseY) {
        for (Entity entity : entities) {
            if (entity instanceof Chicken && isMouseOnEntity(entity, mouseX, mouseY) && entity.getTimeAliveFraction() < 0.96) {
                return true;
            }
        }
        return false;
    }

    private boolean isMouseOnEntity(Entity entity, int mouseX, int mouseY) {
        double pointRadius = 15; // Assuming each entity is drawn as a circle with this radius (half of the diameter)
        boolean inCircle = Math.pow(mouseX - entity.getX() - pointRadius, 2) + Math.pow(mouseY - entity.getY() - pointRadius, 2) <= Math.pow(pointRadius, 2);
        
        //System.out.println("Clicked at " + entity.getX() + "," + entity.getY() + "?" + inCircle);

        return inCircle;
    }

    public ArrayList<Entity> getEntities() {
        return this.entities;
    }
}
