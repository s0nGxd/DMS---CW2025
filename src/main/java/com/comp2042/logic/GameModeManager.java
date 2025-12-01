package com.comp2042.logic;

import com.comp2042.events.GameMode;

public class GameModeManager {
    private GameMode currentMode;
    private int linesCleared;
    private long startTime;
    private int level;
    private boolean gameCompleted;
    private long completionTime = 0;
    private int fallSpeed = 400;

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
        completionTime = 0;
        fallSpeed = 400;
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
                // UPDATED: More accurate level progression
                if (lines > 0) {
                    int newLevel = linesCleared / 10 + 1;
                    if (newLevel > level) {
                        level = newLevel;
                        updateFallSpeed();  // NEW: Update speed when level changes
                    }
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
            case PITFALL: return fallSpeed;  // UPDATED: Use dynamic fallSpeed field
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


    private void updateFallSpeed() {
        fallSpeed = Math.max(100, 400 - (level - 1) * 30);
    }


    public void initializeGameStartTime() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
    }


    public void updateAfterLineClear(int newLinesCleared) {
        addLines(newLinesCleared);
    }


    public int getFallSpeed() {
        return getSpeed();
    }


    public long getCompletionTime() {
        return completionTime;
    }


    public void setCompletionTime(long time) {
        this.completionTime = time;
    }


    public boolean isSprintComplete() {
        return currentMode == GameMode.SPRINT && linesCleared >= 40;
    }


    public boolean isBlitzTimeUp() {
        if (currentMode != GameMode.BLITZ || startTime == 0) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed >= 180000; // 3 minutes
    }


    public GameMode getGameMode() {
        return currentMode;
    }


    public long getGameStartTime() {
        return startTime;
    }


    public int getCurrentLevel() {
        return level;
    }

    public GameMode getCurrentMode() { return currentMode; }
    public int getLinesCleared() { return linesCleared; }
    public int getLevel() { return level; }
    public long getStartTime() { return startTime; }
}