package com.game;

import javax.swing.*;

import com.game.entities.Bomb;
import com.game.entities.Chicken;
import com.game.entities.Entity;
import com.game.entities.EntityManager;

import java.awt.*;
import java.awt.event.*;

import java.lang.Math;

import java.net.URL;

import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private int score;
    private int highScore = 0;
    private int timeElapsed = 0;

    private boolean isGameOver;
    private boolean isMainMenu;
    private boolean isHowToPlayScreen;

    private boolean muteSounds;
    private boolean drawHitboxes;

    private EntityManager entityManager;
    private SoundManager soundManager;
    private SettingsManager settingsManager;

    private final int panelWidth;
    private final int panelHeight;

    private int mouseX = 0;
    private int mouseY = 0;

    public GamePanel() {
        settingsManager = new SettingsManager();
        panelWidth = Integer.parseInt(settingsManager.getSetting("game.window.width"));
        panelHeight = Integer.parseInt(settingsManager.getSetting("game.window.height"));
        muteSounds = false;

        soundManager = new SoundManager(muteSounds);
        entityManager = new EntityManager(soundManager::playSound, this::gameOver, this::refreshPanel, this::updateScore, panelWidth, panelHeight);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //System.out.println("Mouse Pressed at: [" + e.getX() + ", " + e.getY() + "]");
                entityManager.checkEntityClicked(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        isGameOver = false;
        isMainMenu = true;
        muteSounds = false;
        drawHitboxes = false;

        setPreferredSize(new Dimension(panelWidth, panelHeight));

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        showMainMenu();
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

    private void startGame() {
        this.removeAll();

        isMainMenu = false;
        isGameOver = false;
        timeElapsed = 0;
        score = 0;

        entityManager.startGame();

        requestFocusInWindow();

        repaint();
    }

    private void checkMouseOverChicken(int mouseX, int mouseY) {
        if (isMainMenu || isGameOver || isHowToPlayScreen) {
            return;
        }
        if (entityManager.isMouseOverChicken(mouseX, mouseY)) {
            soundManager.playSound("ChickenSquashed.wav");
            gameOver("Squashed a chicken!");
        }
    }

    private void refreshPanel() {
        timeElapsed++;
        entityManager.setSpeedupFactor(speedup());
        checkMouseOverChicken(mouseX, mouseY);
        repaint();
    }
    
    private int speedup() {
        return (int) (Math.log(timeElapsed/1000+2)/(Math.log(2)));
    }
    
    private void updateScore() {
        score++;
    }

    private void gameOver(String message) {
        entityManager.endGame();
        
        this.removeAll();
        isGameOver = true;
        if (score > highScore) {
            highScore = score;
        }

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

    private void showMainMenu() {
        isMainMenu = true;
        // Remove all components from the GamePanel
        this.removeAll();

        // Ensure the MainMenuScreen is visible and added correctly
        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        mainMenuScreen.setPreferredSize(new Dimension(panelWidth, panelHeight)); // for example
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
        
        if (!isMainMenu && !isHowToPlayScreen && ! isGameOver) {
            // Draw your entities with anti-aliasing
            for (Entity entity : entityManager.getEntities()) {
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
    }
    
    private void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (isGameOver) {
                // Logic to return to main menu
                isGameOver = false;
                isMainMenu = true;
                showMainMenu();
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
                showMainMenu();
            }
        }

    }
}