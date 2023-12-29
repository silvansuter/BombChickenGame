package com.game;

import com.game.entities.Entity;
import com.game.entities.EntityManager;

import java.util.function.Consumer;

import java.util.ArrayList;

public class GamePanel {
    private int timeElapsed = 0;

    private String applicationStatus;

    private boolean muteSounds;

    private EntityManager entityManager;
    private SoundManager soundManager;
    private ScoreManager scoreManager;

    private Consumer<String> onGameOver;

    private final int panelWidth;
    private final int panelHeight;

    public GamePanel(Consumer<String> onGameOver, Consumer<String> onPlaySound, Runnable onRefreshPanel) {
        this.onGameOver = onGameOver;

        panelWidth = Integer.parseInt(SettingsManager.getSetting("game.window.width"));
        panelHeight = Integer.parseInt(SettingsManager.getSetting("game.window.height"));
        muteSounds = false;

        soundManager = new SoundManager(muteSounds);
        entityManager = new EntityManager(onPlaySound, this::gameOver, onRefreshPanel, this::updateScore, panelWidth, panelHeight);
        scoreManager = new ScoreManager();

        applicationStatus = "mainMenu";
    }

    /*
    private void createMenu() {
        // Create the menu bar
        menuBar = new JMenuBar();

        // Build the menu
        menu = new JMenu("Options");
        menuBar.add(menu);

        // Menu items
        menuItemStart = new JMenuItem("Start Game");
        menuItemStart.addActionListener(e -> startGame());
        menu.add(menuItemStart);

        menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(e -> System.exit(0));
        menu.add(menuItemExit);
    }
    */

    public void startGame() {
        applicationStatus = "in game";
        timeElapsed = 0;
        scoreManager.resetCurrentScore();

        entityManager.startGame();
    }

    public void gameOver(String message) {
        applicationStatus = "game over";
        onGameOver.accept(message);
        entityManager.endGame();
    }

    public void refresh() {
        timeElapsed++; // Needs to be moved from here.
        entityManager.setSpeedupFactor(HelperFunctions.computeSpeedup(timeElapsed));
    }

    public void checkMouseOverChicken(int mouseX, int mouseY) {
        if (applicationStatus == "in game" && entityManager.isMouseOverChicken(mouseX, mouseY)) {
            soundManager.playSound("ChickenSquashed.wav");
            gameOver("Squashed a chicken!");
        }
    }
    
    private void updateScore() {
        scoreManager.updateScore();
    }

    public int getHighScore() {
        return scoreManager.getHighScore();
    }

    public int getCurrentScore() {
        return scoreManager.getCurrentScore();
    }

    public void saveHighScore(int score) {
        scoreManager.saveHighScore(score);
    }

    public void checkEntityClicked(int x, int y) {
        entityManager.checkEntityClicked(x, y);
    }

    public ArrayList<Entity> getEntities() {
        return entityManager.getEntities();
    }

    public double computeSpeedup() {
        return HelperFunctions.computeSpeedup(timeElapsed);
    }
}