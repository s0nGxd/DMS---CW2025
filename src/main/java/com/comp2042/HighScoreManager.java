package com.comp2042;

import java.io.*;
import java.util.Properties;

public class HighScoreManager {
    private static final String SCORES_FILE = "highscores.properties";
    private static HighScoreManager instance;
    private Properties properties;

    private HighScoreManager() {
        properties = new Properties();
        loadScores();
    }

    // Ensure only one instance
    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }

    private void loadScores() {
        File file = new File(SCORES_FILE);
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file)) {
                properties.load(input);
                System.out.println("High scores loaded successfully.");
            } catch (IOException e) {
                System.out.println("Error loading high scores: " + e.getMessage());
            }
        } else {
            System.out.println("No existing high scores found, will create new file on first save.");
        }
    }

    private void saveScores() {
        try (OutputStream output = new FileOutputStream(SCORES_FILE)) {
            properties.store(output, "TetrisJFX High Scores");
            System.out.println("High scores saved successfully to: " + new File(SCORES_FILE).getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving high scores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // SPRINT: Best time (lowest is better)
    public long getSprintBestTime() {
        String value = properties.getProperty("sprint_time", "999999999");
        return Long.parseLong(value);
    }

    public void setSprintBestTime(long timeMillis) {
        properties.setProperty("sprint_time", String.valueOf(timeMillis));
        saveScores();
    }

    public boolean isSprintNewRecord(long timeMillis) {
        long current = getSprintBestTime();
        return current == 999999999 || timeMillis < current;
    }

    // BLITZ: High score in 3 minutes
    public int getBlitzHighScore() {
        String value = properties.getProperty("blitz_score", "0");
        return Integer.parseInt(value);
    }

    public void setBlitzHighScore(int score) {
        properties.setProperty("blitz_score", String.valueOf(score));
        saveScores();
    }

    public boolean isBlitzNewRecord(int score) {
        return score > getBlitzHighScore();
    }

    // PITFALL: Highest level reached
    public int getPitfallHighLevel() {
        String value = properties.getProperty("pitfall_level", "0");
        return Integer.parseInt(value);
    }

    public void setPitfallHighLevel(int level) {
        properties.setProperty("pitfall_level", String.valueOf(level));
        saveScores();
    }

    public boolean isPitfallNewLevelRecord(int level) {
        return level > getPitfallHighLevel();
    }

    // PITFALL: High score
    public int getPitfallHighScore() {
        String value = properties.getProperty("pitfall_score", "0");
        return Integer.parseInt(value);
    }

    public void setPitfallHighScore(int score) {
        properties.setProperty("pitfall_score", String.valueOf(score));
        saveScores();
    }

    public boolean isPitfallNewScoreRecord(int score) {
        return score > getPitfallHighScore();
    }

    // Format time for display (milliseconds to HH:MM:SS.mmm)
    public static String formatTime(long millis) {
        long hours = (millis / 1000) / 3600;
        long minutes = ((millis / 1000) % 3600) / 60;
        long seconds = (millis / 1000) % 60;
        long milliseconds = millis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }
}