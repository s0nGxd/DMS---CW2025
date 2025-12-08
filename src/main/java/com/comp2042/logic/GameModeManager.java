package com.comp2042.logic;

import com.comp2042.events.GameMode;

/**
 * Manages game mode state, progression, and mode-specific rules.
 *
 * <p>Handles the logic for all four game modes (ZEN, SPRINT, BLITZ, PITFALL):
 * - Tracks lines cleared and game completion
 * - Manages timers for timed modes
 * - Calculates level progression in PITFALL mode
 * - Determine fall speed based on mode and level
 * - Generates status messages for UI display</p>
 *
 * <p>This class encapsulates all mode-specific behavior, allowing the board
 * to focus on core Tetris mechanics while delegating mode rules here.</p>
 */

public class GameModeManager {
    private GameMode currentMode;
    private int linesCleared;
    private long startTime;
    private int level;
    private boolean gameCompleted;
    private long completionTime = 0;
    private int fallSpeed = 400;

    /**
     * Constructs a new GameModeManager with default ZEN mode.
     */
    public GameModeManager() {
        setGameMode(GameMode.ZEN);
    }

    /**
     * Sets the active game mode and resets all state.
     * @param mode game mode to activate
     */
    public void setGameMode(GameMode mode) {
        this.currentMode = mode;
        reset();
    }

    /**
     * Resets all game mode state to initial values.
     * <p>Clears lines, resets timer, sets level to 1, clears completion
     * status, and resets fall speed.</p>
     */
    public void reset() {
        linesCleared = 0;
        startTime = System.currentTimeMillis();
        level = 1;
        gameCompleted = false;
        completionTime = 0;
        fallSpeed = 400;
    }

    /**
     * Adds cleared lines and updates mode state accordingly.
     * <p>For SPRINT: checks if 40 lines reached.
     * For PITFALL: updates level based on total lines (level = lines/10 + 1).</p>
     * @param lines number of lines just cleared
     */
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

    /**
     * Gets fall speed in milliseconds for current mode and level.
     * @return fall delay in milliseconds
     */
    public int getSpeed() {
        switch (currentMode) {
            case ZEN: return 500;
            case SPRINT: return 400;
            case BLITZ: return 350;
            case PITFALL: return fallSpeed;  // UPDATED: Use dynamic fallSpeed field
            default: return 400;
        }
    }

    /**
     * Updates fall speed based on current level in PITFALL mode.
     * <p>Formula: 400ms - (level-1)*30ms, with 100ms minimum.</p>
     */
    private void updateFallSpeed() {
        fallSpeed = Math.max(100, 400 - (level - 1) * 30);
    }

    /**
     * Initializes game start time if not already set.
     * <p>Called when first brick spawns to begin timer.</p>
     */
    public void initializeGameStartTime() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * Updates state after lines are cleared.
     * @param newLinesCleared number of lines just cleared
     */
    public void updateAfterLineClear(int newLinesCleared) {
        addLines(newLinesCleared);
    }

    /**
     * Gets current fall speed in milliseconds.
     * @return fall delay in milliseconds
     */
    public int getFallSpeed() {
        return getSpeed();
    }

    /**
     * Sets completion time when mode objective is met.
     * @param time completion time in milliseconds
     */
    public void setCompletionTime(long time) {
        this.completionTime = time;
    }

    /**
     * Checks if SPRINT mode is complete (40 lines cleared).
     * @return true if 40+ lines cleared in SPRINT mode, false otherwise
     */
    public boolean isSprintComplete() {
        return currentMode == GameMode.SPRINT && linesCleared >= 40;
    }

    /**
     * Checks if BLITZ mode time limit reached (3 minutes).
     * @return true if 3 minutes elapsed in BLITZ mode, false otherwise
     */
    public boolean isBlitzTimeUp() {
        if (currentMode != GameMode.BLITZ || startTime == 0) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed >= 180000; // 3 minutes
    }

    /**
     * Gets current game mode.
     * @return active GameMode
     */
    public GameMode getGameMode() {
        return currentMode;
    }

    /**
     * Gets game start timestamp.
     * @return start time in milliseconds since epoch
     */
    public long getGameStartTime() {
        return startTime;
    }

    /**
     * Gets current level (PITFALL mode).
     * @return current level (1-based)
     */
    public int getCurrentLevel() {
        return level;
    }

    /**
     * Gets total lines cleared in current game.
     * @return lines cleared count
     */
    public int getLinesCleared() { return linesCleared; }
}