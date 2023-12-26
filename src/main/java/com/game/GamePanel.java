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

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {
    private ArrayList<Entity> entities;
    private final Random random;
    private int score;
    private int highScore = 0;
    private int timeElapsed = 0;

    private boolean isGameOver;
    private boolean isMainMenu;
    private boolean isHowToPlayScreen;

    private boolean muteSounds;
    private boolean drawHitboxes;

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
        drawHitboxes = false;

        loadSounds();

        setPreferredSize(new Dimension(800, 600));

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                checkMouseOverChicken(e.getX(), e.getY());
            }
        });

        returnToMainMenu();
    }

    private void checkMouseOverChicken(int mouseX, int mouseY) {
        if (isMainMenu || isGameOver || isHowToPlayScreen) {
            return;
        }
        for (Entity entity : entities) {
            if (entity instanceof Chicken && isEntityClicked(entity, mouseX, mouseY) && entity.getTimeAliveFraction() < 0.96) {
                playSound("ChickenSquashed.wav");
                gameOver("Squashed a chicken!");
                break;
            }
        }
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
        int delay = random.nextInt(1500, 2000)/speedup(); // Random delay between 500ms to 2000ms
        spawnTimer = new Timer(delay, e -> spawnEntity());
        spawnTimer.setRepeats(false); // Ensure the timer only triggers once per scheduling
        spawnTimer.start();
    }

    private void spawnEntity() {
        int pointDiameter = 30;
        int x = random.nextInt(getWidth() - pointDiameter);
        int y = random.nextInt(getHeight() - pointDiameter);
        int timeTillDie = random.nextInt(400, 500)/speedup();
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
        entities = new ArrayList<>();
    
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
            g.drawString("GAME OVER", 30, 70);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString(message,  30, 100); // score is from GamePanel
            g.drawString("Score: " + score, 30, 120); // score is from GamePanel
            g.drawString("Press SPACE to return to the Main Menu", 30, 140);
        }
    }

    private class MainMenuScreen extends JComponent {
        private Image titleImage;
    
        public MainMenuScreen() {
            try {
                URL imageUrl = getClass().getResource("/images/TitleImageNew.png"); // Adjust path if necessary
                ImageIcon icon = new ImageIcon(imageUrl);
                titleImage = icon.getImage();
            } catch (Exception e) {
                e.printStackTrace();
                titleImage = null;
            }
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
    
            if (titleImage != null) {
                g.drawImage(titleImage, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                // Fallback to a plain background if image fails to load
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            
            String titleString = "BOMBS 'N' CHICKS";
            Font titleFont = new Font("Arial", Font.BOLD, 40);
            g.setFont(titleFont);
            FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);
            g.drawString(titleString, getXforMiddle(titleString, titleFontMetrics), 40);

            Font titleScreenFont = new Font("Arial", Font.PLAIN, 20);
            g.setFont(titleScreenFont);
            FontMetrics highScoreFontMetrics = g.getFontMetrics(titleScreenFont);

            String pressSpaceString = "Press SPACE to Start";
            g.drawString(pressSpaceString, getXforMiddle(pressSpaceString, highScoreFontMetrics), 60);

            String highScoreString = "HighScore: " + highScore;
            g.drawString(highScoreString, getXforMiddle(highScoreString, highScoreFontMetrics), 80);

            g.drawString("'H': Help", 10 , 20);


            //g.setColor(Color.BLACK);
            //g.setFont(new Font("Arial", Font.BOLD, 40));
            //g.drawString("CHICKS 'N' BOMBS", 200, 40);
            //g.setFont(new Font("Arial", Font.PLAIN, 20));
            //g.drawString("HighScore: " + highScore, 30, 100);
            //g.drawString("Press SPACE to Start", 30, 120);
        }
    }

    private int getXforMiddle(String titleString, FontMetrics fontMetrics) {
        int textWidth = fontMetrics.stringWidth(titleString);
        return (getWidth() - textWidth) / 2; // Calculate X-coordinate for centering
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

    private class HowToPlayScreen extends JComponent {

        public HowToPlayScreen() {
            super();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the game over screen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("HOW TO PLAY", 30, 70);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Defuse (click) the bombs.", 30, 120); // score is from GamePanel
            g.drawString("Do not squash (touch) any chicken.", 30, 140);
            g.drawString("Press <H> to return to Main Menu.", 30, 160);

        }
    }

    private void showHowToPlayScreen() {
        isMainMenu = false;
        isHowToPlayScreen = true;
        // Remove all components from the GamePanel
        this.removeAll();

        // Ensure the MainMenuScreen is visible and added correctly
        HowToPlayScreen howToPlayScreen = new HowToPlayScreen();
        howToPlayScreen.setPreferredSize(new Dimension(800, 600)); // for example
        this.setLayout(new BorderLayout()); // Using BorderLayout for simplicity
        this.add(howToPlayScreen, BorderLayout.CENTER);

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
            BufferedImage image = entity.getImage(); // Get the Bomb.png image
            if (entity instanceof Bomb) {
                g2d.drawImage(image, entity.getX()-14, entity.getY()-20, 60, 60, null);
            }
            else if (entity instanceof Chicken) {
                g2d.drawImage(image, entity.getX()-11, entity.getY()-19, 60, 60, null);
            }
            
            if (drawHitboxes) {
                // Set a semi-transparent color for the hitbox
                Color hitboxColor = new Color(255, 0, 0, 128); // Red color with 50% transparency
                g2d.setColor(hitboxColor);

                // Draw the hitbox as a semi-transparent circle over the image
                g2d.drawOval(entity.getX(), entity.getY(), 30, 30); // Draw the hitbox as an outline
            }
            
            /*
            g2d.setColor(entity.getColor());
            g2d.fillOval(entity.getX(), entity.getY(), 30, 30);
            */
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
        if (e.getKeyCode() == KeyEvent.VK_H) {
            if (isMainMenu) {
                showHowToPlayScreen();
            }
            else if (isHowToPlayScreen) {
                returnToMainMenu();
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