package com.comp2042.logic;

import java.awt.Point;

/**
 * Calculates the landing position (ghost position) for the current falling brick.
 *
 * <p>The ghost brick is a visual preview showing where the current piece will
 * land if hard dropped immediately. This calculator simulates the fall by
 * iteratively moving down until collision is detected.</p>
 *
 * <p>Calculation is performed every frame to ensure real-time updates as the
 * player moves or rotates the brick.</p>
 */

public class GhostBrickCalculator {

    /**
     * Calculates the Y-coordinate where the brick will land.
     *
     * <p>Algorithm:
     * 1. Start at current brick position
     * 2. Test moving down one row at a time
     * 3. Check collision at each test position
     * 4. Return last valid position before collision</p>
     *
     * @param currentGameMatrix current state of game board
     * @param currentShape shape matrix of falling brick
     * @param currentOffset current X,Y position of brick
     * @return Y-coordinate where brick will land (ghost position)
     */
    public int calculateGhostPosition(int[][] currentGameMatrix,
                                      int[][] currentShape,
                                      Point currentOffset) {
        // Start from current Y position
        int ghostY = (int) currentOffset.getY();
        int currentX = (int) currentOffset.getX();

        // Create a copy of the game matrix to test collisions
        int[][] matrixCopy = MatrixOperations.copy(currentGameMatrix);

        // Keep moving down until collision is detected
        while (true) {
            int testY = ghostY + 1;

            // Check if next position down would cause collision
            boolean collision = MatrixOperations.intersect(
                    matrixCopy,
                    currentShape,
                    currentX,
                    testY
            );

            if (collision) {
                // Can't go further down, current ghostY is the landing position
                return ghostY;
            }

            // Can go further down
            ghostY = testY;
        }
    }

    /**
     * Calculates ghost position using a BrickRotator.
     *
     * <p>Method that extracts current shape from rotator
     * and delegates to main calculation method.</p>
     *
     * @param currentGameMatrix current state of game board
     * @param rotator brick rotator containing current shape
     * @param currentOffset current X,Y position of brick
     * @return Y-coordinate where brick will land
     */
    public int calculateGhostPosition(int[][] currentGameMatrix,
                                      BrickRotator rotator,
                                      Point currentOffset) {
        return calculateGhostPosition(
                currentGameMatrix,
                rotator.getCurrentShape(),
                currentOffset
        );
    }
}