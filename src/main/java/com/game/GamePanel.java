package com.game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import java.lang.Math;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;

import java.net.URL;


public class GamePanel extends JPanel {
    private ArrayList<Entity> entities;
    private final Random random;
    private int score;
    private int highScore = 0;
    private int timeElapsed = 0;

    private boolean isGameOver;
    private boolean isMainMenu;
    private boolean muteSounds;

    private Map<String, Clip> soundClips = new HashMap<>();

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItemStart;
    private JMenuItem menuItemExit;


    // Initialize and store timers as fields so you can stop them
    Timer spawnTimer = new Timer(1000, e -> spawnEntity());
    Timer refreshTimer = new Timer(16, e -> repaint());
    Timer updateEntitiesTimer = new Timer(16, e -> updateEntities());

    public GamePanel() {
        entities = new ArrayList<>();
        random = new Random();
        score = 0;
        isGameOver = false;
        isMainMenu = true;
        muteSounds = false;

        loadSounds();

        setPreferredSize(new Dimension(800, 600));

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        returnToMainMenu();
    }

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

    private void startGame() {
        this.removeAll();
        createMenu();
        this.add(menuBar, BorderLayout.SOUTH);

        isGameOver = false;
        score = 0;
        entities = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //System.out.println("Mouse Pressed at: [" + e.getX() + ", " + e.getY() + "]");
                checkEntityClicked(e.getX(), e.getY());
            }
        });

        // Timer to spawn points
        scheduleNextSpawn();

        // Timer for game refresh
        refreshTimer.start();

        updateEntitiesTimer.start();

        repaint();
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
        int timeTillDie = random.nextInt(350, 500)/speedup();
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
        this.removeAll();
        isGameOver = true;
        if (score > highScore) {
            highScore = score;
        }
    
        // Stop the timers
        spawnTimer.stop();
        refreshTimer.stop();
        updateEntitiesTimer.stop();
    
        // Add the game over screen to the panel
        GameOverScreen gameOverScreen = new GameOverScreen(message);
        gameOverScreen.setBounds(0, 0, getWidth(), getHeight());
        add(gameOverScreen);
        requestFocusInWindow();
        repaint();
    }

    private class GameOverScreen extends JComponent {

        String message;

        public GameOverScreen(String message) {
            super();
            this.message = message;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the game over screen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", getWidth() / 2 - 100, getHeight() / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString(message,  getWidth() / 2 - 100, getHeight() / 2 - 20); // score is from GamePanel
            g.drawString("Score: " + score, getWidth() / 2 - 50, getHeight() / 2); // score is from GamePanel
            g.drawString("Press SPACE to return to the Main Menu", getWidth() / 2 - 150, getHeight() / 2 + 50);
        }
    }

    private class MainMenuScreen extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the game over screen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("CHICKS 'N' BOMBS", getWidth() / 2 - 100, getHeight() / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("HighScore: " + highScore, getWidth() / 2 - 50, getHeight() / 2); // score is from GamePanel
            g.drawString("Press SPACE to Start", getWidth() / 2 - 150, getHeight() / 2 + 50);
        }
    }

    private void returnToMainMenu() {
        isMainMenu = true;
        // Remove all components from the GamePanel
        this.removeAll();

        // Ensure the MainMenuScreen is visible and added correctly
        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        mainMenuScreen.setPreferredSize(new Dimension(800, 600)); // for example
        this.setLayout(new BorderLayout()); // Using BorderLayout for simplicity
        this.add(mainMenuScreen, BorderLayout.CENTER);

        // Refresh the panel
        this.revalidate();
        this.repaint();

         // Request focus so the GamePanel can detect key presses
        requestFocusInWindow();
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
        g2d.drawString("Score: " + score, 10, 15);
    }

    public void playSound(String soundFileName) {
        if (muteSounds) {
            return;
        }

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
    
    private void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (isGameOver) {
                // Logic to return to main menu
                isGameOver = false;
                isMainMenu = true;
                returnToMainMenu();
            } else if (isMainMenu) {
                // Logic to start the game from the main menu
                isMainMenu = false;
                startGame();
            }
        }
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