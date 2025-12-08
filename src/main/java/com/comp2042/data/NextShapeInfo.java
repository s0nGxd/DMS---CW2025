package com.comp2042.data;

import com.comp2042.logic.MatrixOperations;

/**
 * Data container holding information about the next brick rotation state.
 * Contains both the shape matrix and the rotation position index.
 */

public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a NextShapeInfo with the specified shape and position.
     * @param shape the 2D array representing the brick shape
     * @param position the rotation state index (0-3)
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = MatrixOperations.copy(shape);
        this.position = position;
    }

    /**
     * Gets a deep copy of the shape matrix.
     * @return a 2D array representing the brick shape
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the rotation position index.
     * @return the rotation state (0-3)
     */
    public int getPosition() {
        return position;
    }
}
