package com.game;

import javax.swing.*;

import com.game.entities.BombImageCache;

public class BombsNChicksMain extends JFrame {
    private final GameGUI gameGUI;

    public BombsNChicksMain() {
        super("Bombs 'n' Chicks");
        SettingsManager.init();
        BombImageCache.init();
        gameGUI = new GameGUI();
        add(gameGUI);
        setSize(Integer.parseInt(SettingsManager.getSetting("game.window.width")), Integer.parseInt(SettingsManager.getSetting("game.window.height")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack(); // Adjusts size to fit the game panel
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BombsNChicksMain game = new BombsNChicksMain();
            game.setVisible(true);
        });
    }
}