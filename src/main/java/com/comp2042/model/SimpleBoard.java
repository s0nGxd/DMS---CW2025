package com.comp2042.model;

import com.comp2042.events.GameMode;
import com.comp2042.data.Score;
import com.comp2042.logic.*;
import com.comp2042.data.NextShapeInfo;
import com.comp2042.data.ViewData;
import com.comp2042.data.ClearRow;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main implementation of the game board containing core Tetris game logic.
 *
 * <p>This class manages the game state including the board matrix, current brick position,
 * rotation, collision detection, line clearing, and game mode integration. It implements
 * the Super Rotation System (SRS) for official Tetris rotation behavior with wall kicks.</p>
 *
 * <p>Key responsibilities:
 * - Brick movement and collision detection
 * - SRS rotation with wall kicks
 * - Hold function management
 * - Line clearing and scoring
 * - Game mode state management
 * - Ghost brick position calculation</p>
 *
 * <p>This class delegates specific tasks to:
 * - BrickRotator for rotation state management
 * - GhostBrickCalculator for landing preview
 * - GameModeManager for mode-specific rules
 * - BrickGenerator for piece generation</p>
 */

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private final GhostBrickCalculator ghostBrickCalculator;
    private final GameModeManager gameModeManager;

    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick heldBrick = null;
    private boolean heldThisTurn = false;

    /**
     * Constructs a new game board with specified dimensions.
     *
     * <p>Initializes all game components including brick generator, rotator,
     * ghost calculator, game mode manager, and creates an empty game matrix.</p>
     *
     * @param width board width in blocks (typically 10)
     * @param height board height in blocks (typically 25, with top 5 hidden)
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();

        // Initialize components from their class
        ghostBrickCalculator = new GhostBrickCalculator();
        gameModeManager = new GameModeManager();
    }

    /**
     * Sets the current game mode.
     * @param mode game mode to activate (ZEN, SPRINT, BLITZ, or PITFALL)
     */
    @Override
    public void setGameMode(GameMode mode) {
        gameModeManager.setGameMode(mode);
    }

    /**
     * Gets the current game mode.
     * @return active game mode
     */
    @Override
    public GameMode getGameMode() {
        return gameModeManager.getGameMode();
    }

    /**
     * Gets total lines cleared in current game.
     * @return total lines cleared
     */
    @Override
    public int getLinesCleared() {
        return gameModeManager.getLinesCleared();
    }

    /**
     * Gets timestamp when current game started.
     * @return start time in milliseconds since epoch, or 0 if not started
     */
    @Override
    public long getGameStartTime() {
        return gameModeManager.getGameStartTime();
    }

    /**
     * Gets current level in PITFALL mode.
     * @return current level, calculated as (linesCleared / 10) + 1
     */
    @Override
    public int getCurrentLevel() {
        return gameModeManager.getCurrentLevel();
    }

    /**
     * Gets current fall speed based on game mode and level.
     * @return fall delay in milliseconds
     */
    @Override
    public int getFallSpeed() {
        return gameModeManager.getFallSpeed();
    }

    /**
     * Attempts to move the current brick down by one row.
     * <p>Checks for collision before moving. If blocked, returns false
     * indicating the brick should lock in place.</p>
     * @return true if brick moved successfully, false if blocked
     */
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to move the current brick left by one column.
     * <p>Checks for collision with walls or other blocks before moving.</p>
     * @return true if brick moved successfully, false if blocked
     */
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to move the current brick right by one column.
     * <p>Checks for collision with walls or other blocks before moving.</p>
     * @return true if brick moved successfully, false if blocked
     */
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Determines brick type for SRS kick table selection.
     * <p>Searches current brick shape for first non-zero value representing
     * the brick's type ID (1=I, 2=J, 3=L, 4=O, 5=S, 6=T, 7=Z).</p>
     * @return brick type ID, or 0 if shape is empty
     */
    private int getBrickType() {
        int[][] shape = brickRotator.getCurrentShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    return shape[i][j];
                }
            }
        }
        return 0;
    }

    /**
     * Swaps the current brick with the held brick.
     * <p>If no brick is held, stores current brick and spawns next piece.
     * If brick already held, swaps with current brick. Limited to once per
     * piece drop.</p>
     * @return true if hold succeeded, false if already held this turn
     */
    @Override
    public boolean holdCurrentBrick() {
        if (heldThisTurn) {
            return false;
        }

        Brick currentBrick = brickRotator.getCurrentBrick();

        if (heldBrick == null) {
            // First hold
            heldBrick = currentBrick;
            Brick newBrick = brickGenerator.getBrick();
            brickRotator.setBrick(newBrick);
            currentOffset = new Point(3, 0);
        } else {
            // Swap hold
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(3, 0);
        }

        heldThisTurn = true;
        return true;
    }

    /**
     * Performs SRS rotation with wall kick attempts.
     * <p>SRS rotation logic that retrieves appropriate wall kick offsets
     * based on brick type and rotation direction, then tests each offset in
     * priority order until a valid position is found.</p>
     * @param nextShape the next rotation state to attempt
     * @return true if rotation succeeded at any kick offset, false if all failed
     */
    private boolean SRSRotation(NextShapeInfo nextShape) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int currentState = brickRotator.getCurrentState();
        int targetState = nextShape.getPosition();
        int brickType = getBrickType();

        // Get the official SRS kick offsets for rotation
        Point[] kickOffsets = SRSKickData.getKickOffsets(brickType, currentState, targetState);

        // Try each kick offset in order until one succeed
        for (Point kick : kickOffsets) {
            int testX = (int) currentOffset.getX() + kick.x;
            int testY = (int) currentOffset.getY() - kick.y;

            if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), testX, testY)) {
                // Determine successful rotation with this kick
                currentOffset.setLocation(testX, testY);
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }
        // All kick attempts failed
        return false;
    }

    /**
     * Attempts to rotate the current brick counter-clockwise using SRS.
     * <p>Implements Super Rotation System with wall kick tests. If basic rotation
     * fails, tries up to 5 different kick offsets to find a valid position.</p>
     * @return true if rotation succeeded (including wall kicks), false if all attempts failed
     */
    @Override
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShapeCounterClockwise();
        return SRSRotation(nextShape);
    }

    /**
     * Attempts to rotate the current brick clockwise using SRS.
     * <p>Implements Super Rotation System with wall kick tests. If basic rotation
     * fails, tries up to 5 different kick offsets to find a valid position.</p>
     * @return true if rotation succeeded (including wall kicks), false if all attempts failed
     */
    @Override
    public boolean rotateRightBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShapeClockwise();
        return SRSRotation(nextShape);
    }

    /**
     * Creates and spawns a new brick at the top of the board.
     * <p>Retrieves next brick from generator, positions at spawn point (x=3, y=0),
     * resets hold flag, and initializes game start time on first brick.</p>
     * @return true if new brick collides immediately (game over), false if spawn succeeded
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(3, 0);

        // Reset hold flag when new brick spawns
        heldThisTurn = false;

        // Initialize game start time on first brick
        gameModeManager.initializeGameStartTime();

        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Returns the current state of the game board matrix.
     * <p>Matrix represents locked-in pieces only. The falling piece is
     * rendered separately using getViewData().</p>
     * @return 2D array where 0 is empty, other values represent brick types
     */
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Gathers all data needed for rendering the current game state.
     * <p>Creates ViewData containing current brick shape and position,
     * ghost brick landing position, next 4 upcoming bricks, and held brick if any.</p>
     * @return immutable ViewData object with all rendering information
     */
    @Override
    public ViewData getViewData() {
        // Use GhostBrickCalculator to get ghost position
        int ghostY = ghostBrickCalculator.calculateGhostPosition(currentGameMatrix, brickRotator, currentOffset);

        int[][] heldBrickData = (heldBrick != null)
                ? heldBrick.getShapeMatrix().get(0)  // Always show first rotation
                : null;
        // Get next 4 bricks data
        List<int[][]> nextBricksData = new ArrayList<>();
        List<Brick> nextBricks = brickGenerator.getNextBricks(4);
        for (Brick brick : nextBricks) {
            nextBricksData.add(brick.getShapeMatrix().get(0));
        }
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0), ghostY, heldBrickData, nextBricksData);
    }

    /**
     * Locks the current brick into the board matrix.
     *
     * <p>Merges falling brick with background by copying its cells into
     * board matrix at current position. After this, brick becomes part
     * of static board.</p>
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Checks for and removes complete horizontal lines.
     * <p>Scans board from bottom to top, identifies complete rows, removes them,
     * and shifts remaining rows down. Updates game mode manager with cleared
     * line count for progression tracking.</p>
     * <p>Score bonus: 50 × lines² (1=50, 2=200, 3=450, 4=800)</p>
     * @return ClearRow with lines cleared count, new board state, and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();

        // Update game mode manager with cleared lines
        gameModeManager.updateAfterLineClear(clearRow.getLinesRemoved());

        return clearRow;
    }

    /**
     * Returns the score object for UI binding.
     * @return Score object managing current game score
     */
    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Sets completion time for timed game modes.
     * @param time completion time in milliseconds
     */
    public void setCompletionTime(long time) {
        gameModeManager.setCompletionTime(time);
    }

    /**
     * Resets board to initial state for a new game.
     * <p>Clears board matrix, resets score, clears held brick, resets
     * game mode state, and spawns first piece.</p>
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        // Resets the hold brick function
        heldBrick = null;

        // Reset game mode variables
        gameModeManager.reset();

        createNewBrick();
    }
}
