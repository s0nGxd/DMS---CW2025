package com.comp2042.data;

import com.comp2042.logic.MatrixOperations;

/**
 * Data container for information about cleared rows.
 * Contains the number of lines removed, the updated board matrix, and score bonus.
 */

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Constructs a ClearRow with cleared line information.
     * @param linesRemoved the number of lines that were cleared
     * @param newMatrix the updated game board after line removal
     * @param scoreBonus the points awarded for clearing these lines
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines that were cleared.
     * @return the count of cleared lines
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets a copy of the updated game board matrix.
     * @return 2D array representing the board after line removal
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus for the cleared lines.
     * @return the points awarded (scales with number of lines cleared)
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
