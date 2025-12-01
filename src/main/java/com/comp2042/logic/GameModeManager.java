package com.comp2042.logic;

import com.comp2042.events.GameMode;

public class GameModeManager {
    private GameMode currentMode;
    private int linesCleared;
    private long startTime;
    private int level;
    private boolean gameCompleted;

    public GameModeManager() {
        setGameMode(GameMode.ZEN);
    }

    public void setGameMode(GameMode mode) {
        this.currentMode = mode;
        reset();
    }

    public void reset() {
        linesCleared = 0;
        startTime = System.currentTimeMillis();
        level = 1;
        gameCompleted = false;
    }

    public void addLines(int lines) {
        if (gameCompleted) return;

        linesCleared += lines;

        switch (currentMode) {
            case SPRINT:
                if (linesCleared >= 40) {
                    gameCompleted = true;
                }
                break;
            case PITFALL:
                if (linesCleared >= level * 10) {
                    level++;
                }
                break;
        }
    }

    public void update() {
        if (currentMode == GameMode.BLITZ && !gameCompleted) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= 180000) { // 3 minutes
                gameCompleted = true;
            }
        }
    }

    public int getSpeed() {
        switch (currentMode) {
            case ZEN: return 500;
            case SPRINT: return 400;
            case BLITZ: return 350;
            case PITFALL: return Math.max(100, 400 - (level * 30));
            default: return 400;
        }
    }

    public String getStatus() {
        switch (currentMode) {
            case SPRINT: return "SPRINT: " + linesCleared + "/40 lines";
            case BLITZ:
                long remaining = Math.max(0, 180000 - (System.currentTimeMillis() - startTime));
                long minutes = remaining / 60000;
                long seconds = (remaining % 60000) / 1000;
                return String.format("BLITZ: %02d:%02d", minutes, seconds);
            case PITFALL: return "PITFALL: Level " + level + " (" + linesCleared + " lines)";
            case ZEN: return "ZEN: " + linesCleared + " lines cleared";
            default: return "";
        }
    }

    public boolean isGameOver() {
        return gameCompleted;
    }

    public String getCompletionMessage() {
        switch (currentMode) {
            case SPRINT: return "Sprint Complete! Cleared 40 lines!";
            case BLITZ: return "Time's up! Blitz mode completed!";
            case PITFALL: return "Reached Level " + level + " in Pitfall mode!";
            case ZEN: return "Zen session: " + linesCleared + " lines cleared";
            default: return "Game Complete!";
        }
    }

    // Getters
    public GameMode getCurrentMode() { return currentMode; }
    public int getLinesCleared() { return linesCleared; }
    public int getLevel() { return level; }
    public long getStartTime() { return startTime; }
}