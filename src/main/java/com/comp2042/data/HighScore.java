package com.comp2042.data;

import java.io.*;
import java.util.Properties;

/**
 * Singleton manager for persistent high score storage across game modes.
 * Stores and retrieves best times, scores, and levels using Java Properties.
 */

public class HighScore {
    private static final String SCORES_FILE = "highscores.properties";
    private static HighScore instance;
    private Properties properties;

    private HighScore() {
        properties = new Properties();
        loadScores();
    }

    /**
     * Gets the singleton instance of HighScore.
     * @return the shared HighScore instance
     */
    // Ensure only one instance
    public static HighScore getInstance() {
        if (instance == null) {
            instance = new HighScore();
        }
        return instance;
    }

    /**
     * Loads high scores from the properties file.
     */
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

    /**
     * Saves current high scores to the properties file.
     */
    private void saveScores() {
        try (OutputStream output = new FileOutputStream(SCORES_FILE)) {
            properties.store(output, "TetrisJFX High Scores");
            System.out.println("High scores saved successfully to: " + new File(SCORES_FILE).getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving high scores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the best completion time for Sprint mode.
     * @return the best time in milliseconds, or 999999999 if no record exists
     */
    // SPRINT: Best time (lowest is better)
    public long getSprintBestTime() {
        String value = properties.getProperty("sprint_time", "999999999");
        return Long.parseLong(value);
    }

    /**
     * Sets a new best time for Sprint mode.
     * @param timeMillis the completion time in milliseconds
     */
    public void setSprintBestTime(long timeMillis) {
        properties.setProperty("sprint_time", String.valueOf(timeMillis));
        saveScores();
    }

    /**
     * Checks if a Sprint time is a new record.
     * timeMillis is the time to check in milliseconds
     * @return true if this is a new record, false otherwise
     */
    public boolean isSprintNewRecord(long timeMillis) {
        long current = getSprintBestTime();
        return current == 999999999 || timeMillis < current;
    }

    /**
     * Gets the high score for Blitz mode.
     * @return the highest score achieved in Blitz mode
     */
    // BLITZ: High score in 3 minutes
    public int getBlitzHighScore() {
        String value = properties.getProperty("blitz_score", "0");
        return Integer.parseInt(value);
    }

    /**
     * Sets a new high score for Blitz mode.
     * @param score the new high score
     */
    public void setBlitzHighScore(int score) {
        properties.setProperty("blitz_score", String.valueOf(score));
        saveScores();
    }

    /**
     * Checks if a Blitz score is a new record.
     * @param score the score to check
     * @return true if this is a new record, false otherwise
     */
    public boolean isBlitzNewRecord(int score) {
        return score > getBlitzHighScore();
    }

    /**
     * Gets the highest level reached in Pitfall mode.
     * @return the highest level achieved
     */
    // PITFALL: Highest level reached
    public int getPitfallHighLevel() {
        String value = properties.getProperty("pitfall_level", "0");
        return Integer.parseInt(value);
    }

    /**
     * Sets a new high level for Pitfall mode.
     * @param level the new high level
     */
    public void setPitfallHighLevel(int level) {
        properties.setProperty("pitfall_level", String.valueOf(level));
        saveScores();
    }

    /**
     * Checks if a Pitfall level is a new record.
     * @param level the level to check
     * @return true if this is a new record, false otherwise
     */

    public boolean isPitfallNewLevelRecord(int level) {
        return level > getPitfallHighLevel();
    }

    /**
     * Gets the high score for Pitfall mode.
     * @return the highest score achieved in Pitfall mode
     */
    // PITFALL: High score
    public int getPitfallHighScore() {
        String value = properties.getProperty("pitfall_score", "0");
        return Integer.parseInt(value);
    }

    /**
     * Sets a new high score for Pitfall mode.
     * @param score the new high score
     */
    public void setPitfallHighScore(int score) {
        properties.setProperty("pitfall_score", String.valueOf(score));
        saveScores();
    }

    /**
     * Checks if a Pitfall score is a new record.
     * @param score the score to check
     * @return true if this is a new record, false otherwise
     */
    public boolean isPitfallNewScoreRecord(int score) {
        return score > getPitfallHighScore();
    }

    /**
     * Formats a time in milliseconds to HH:MM:SS.mmm format.
     * @param millis the time in milliseconds
     * @return formatted time string
     */
    // Format time for display (milliseconds to HH:MM:SS.mmm)
    public static String formatTime(long millis) {
        long hours = (millis / 1000) / 3600;
        long minutes = ((millis / 1000) % 3600) / 60;
        long seconds = (millis / 1000) % 60;
        long milliseconds = millis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }
}