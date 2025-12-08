package com.comp2042.data;

import com.comp2042.logic.MatrixOperations;

import java.util.List;

/**
 * Data container for all visual information needed to render the game state.
 * Contains current brick data, position, ghost brick position, held brick, and next bricks.
 */

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final int ghostPosition;
    private final int[][] heldBrickData;
    private final List<int[][]> nextBricksData;

    /**
     * Constructs a ViewData with complete game state information.
     *
     * @param brickData the current brick's shape matrix
     * @param xPosition the horizontal position of the current brick
     * @param yPosition the vertical position of the current brick
     * @param nextBrickData the next brick's shape matrix
     * @param ghostPosition the Y position where the current brick would land
     * @param heldBrickData the held brick's shape matrix, or null if none
     * @param nextBricksData list of upcoming brick shape matrices
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, int ghostPosition, int[][] heldBrickData, List<int[][]> nextBricksData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostPosition = ghostPosition;
        this.heldBrickData = heldBrickData;
        this.nextBricksData = nextBricksData;
    }

    /**
     * Gets a copy of the current brick's shape matrix.
     * @return 2D array representing the current brick shape
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the horizontal position of the current brick.
     * @return the X coordinate on the game board
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the vertical position of the current brick.
     * @return the Y coordinate on the game board
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets the list of upcoming brick shape matrices.
     * @return list of 2D arrays representing future bricks
     */
    // Method to get next brick
    public List<int[][]> getNextBricksData() {
        return nextBricksData;
    }

    /**
     * Gets the ghost brick landing position.
     * @return the Y coordinate where the current brick would land if dropped
     */
    public int getGhostPosition() {
        return ghostPosition;
    }

    /**
     * Gets a copy of the held brick's shape matrix.
     * @return 2D array representing the held brick, or null if no brick is held
     */
    // Saving held brick data for display
    public int[][] getHeldBrickData() {
        return heldBrickData != null ? MatrixOperations.copy(heldBrickData) : null;
    }
}
