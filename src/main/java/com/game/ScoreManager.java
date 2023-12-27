package com.game;

import java.io.*;

public class ScoreManager {
    private int currentScore;
    private int highScore;
    private static final String HIGH_SCORE_FILE = "highscore.txt";

    public ScoreManager() {
        this.currentScore = 0;
        this.highScore = loadHighScore();
    }

    public void updateScore() {
        currentScore++;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void resetCurrentScore() {
        this.currentScore = 0;
    }

    public void resetHighScore() {
        this.highScore = 0;
    }

    public void saveHighScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.highScore = score;
    }

    private int loadHighScore() {
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            saveHighScore(0); // Create the file with a high score of 0 if it doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return line != null ? Integer.parseInt(line) : 0;
        } catch (IOException e) {
            return 0;
        }
    }
}
