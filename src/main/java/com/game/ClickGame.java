package com.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ClickGame extends JFrame {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final GamePanel gamePanel;

    public ClickGame() {
        super("Click Game");
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