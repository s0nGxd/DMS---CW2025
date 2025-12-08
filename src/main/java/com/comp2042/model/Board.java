package com.comp2042.model;

import com.comp2042.data.ClearRow;
import com.comp2042.events.GameMode;
import com.comp2042.data.Score;
import com.comp2042.data.ViewData;

/**
 * Defines the interface for game board implementations in TetrisJFX.
 *
 * <p>This interface specifies all operations that a Tetris board must support,
 * including brick movement, rotation, collision detection, line clearing,
 * and game mode.</p>
 */

public interface Board {
    /**
     * Attempts to move the current brick down by one row.
     * @return true if movement succeeded, false if blocked (brick should lock)
     */
    boolean moveBrickDown();
    /**
     * Attempts to move the current brick left by one column.
     * @return true if movement succeeded, false if blocked by wall or other bricks
     */
    boolean moveBrickLeft();
    /**
     * Attempts to move the current brick right by one column.
     * @return true if movement succeeded, false if blocked by wall or other bricks
     */
    boolean moveBrickRight();
    /**
     * Attempts to rotate the current brick counter-clockwise.
     * <p>Uses Super Rotation System (SRS) with wall kicks.</p>
     * @return true if rotation succeeded (including wall kicks), false if all attempts failed
     */
    boolean rotateLeftBrick();
    /**
     * Attempts to rotate the current brick clockwise.
     * <p>Uses Super Rotation System (SRS) with wall kicks.</p>
     * @return true if rotation succeeded (including wall kicks), false if all attempts failed
     */
    boolean rotateRightBrick();
    /**
     * Swaps the current brick with the held brick.
     * @return true if hold succeeded, false if already held this turn
     */
    boolean holdCurrentBrick();
    /**
     * Spawns a new brick at the top of the board.
     * @return true if spawn collision detected (game over), false if spawn succeeded
     */
    boolean createNewBrick();
    /**
     * Returns the current game board matrix containing locked pieces.
     * @return 2D array where 0 is empty and other values represent brick types
     */
    int[][] getBoardMatrix();
    /**
     * Gathers all data needed for rendering the current game state.
     * @return ViewData object containing brick, ghost, hold, and next piece information
     */
    ViewData getViewData();
    /**
     * Locks the current brick into the board matrix permanently.
     */
    void mergeBrickToBackground();
    /**
     * Checks for and removes complete horizontal lines.
     * @return ClearRow object with cleared line count, new matrix, and score bonus
     */
    ClearRow clearRows();
    /**
     * Returns the score object for UI binding.
     * @return the Score object managing current game score
     */
    Score getScore();
    /**
     * Resets the board to initial state for a new game.
     */
    void newGame();
    /**
     * Sets the active game mode.
     * @param mode the game mode to activate (ZEN, SPRINT, BLITZ, or PITFALL)
     */
    void setGameMode(GameMode mode);
    /**
     * Gets the current game mode.
     * @return the active game mode
     */
    GameMode getGameMode();
    /**
     * Gets the total number of lines cleared in current game.
     * @return total lines cleared
     */
    int getLinesCleared();
    /**
     * Gets the timestamp when current game started.
     * @return start time in milliseconds since epoch, or 0 if not started
     */
    long getGameStartTime();
    /**
     * Gets the current level in PITFALL mode.
     * @return current level
     */
    int getCurrentLevel();
    /**
     * Gets the current fall speed based on game mode and level.
     * @return fall delay in milliseconds
     */
    int getFallSpeed();
}
