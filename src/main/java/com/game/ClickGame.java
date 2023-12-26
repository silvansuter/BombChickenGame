package com.game;

import javax.swing.*;

import com.game.entities.BombImageCache;

public class ClickGame extends JFrame {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final GamePanel gamePanel;

    public ClickGame() {
        super("Bombs 'n' Chicks");
        BombImageCache.init();
        gamePanel = new GamePanel();
        add(gamePanel);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack(); // Adjusts size to fit the game panel
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClickGame game = new ClickGame();
            game.setVisible(true);
        });
    }
}