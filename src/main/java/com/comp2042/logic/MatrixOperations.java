package com.comp2042.logic;

import com.comp2042.data.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing static methods for matrix operations.
 *
 * <p>Contains all core matrix manipulation logic:
 * - Collision detection (intersect)
 * - Brick placement (merge)
 * - Matrix copying (copy, deepCopyList)
 * - Line clearing (checkRemoving)</p>
 *
 * <p>All methods are static and this class cannot be instantiated.
 * These operations forms the foundation of the game's physics and grid logic.</p>
 */

public class MatrixOperations {

    /**
     * Checks if a brick collides with the board or goes out of bounds.
     *
     * <p>Tests each non-zero cell of the brick shape against the board.
     * Collision occurs if brick cell overlaps a filled board cell or
     * is outside board boundaries.</p>
     *
     * @param matrix game board matrix to test against
     * @param brick brick shape matrix to test
     * @param x X offset where brick would be placed
     * @param y Y offset where brick would be placed
     * @return true if collision detected, false if position is valid
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if coordinates are outside board boundaries.
     *
     * @param matrix board matrix
     * @param targetX X coordinate to check
     * @param targetY Y coordinate to check
     * @return true if out of bounds, false if within board
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Creates a deep copy of a 2D integer array.
     *
     * <p>Each row is independently copied to ensure modifications to
     * the copy do not affect the original matrix.</p>
     *
     * @param original matrix to copy
     * @return new matrix with same values as original
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick into the board matrix at specified position.
     *
     * <p>Creates copy of board and places brick's non-zero cells into
     * the copy at given offset. Original board is not modified.</p>
     *
     * @param filledFields current board state
     * @param brick brick shape to merge
     * @param x X offset for brick placement
     * @param y Y offset for brick placement
     * @return new matrix with brick merged into board
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /**
     * Identifies and removes complete horizontal lines from board.
     *
     * <p>Scans each row from bottom to top. Complete rows (all cells filled)
     * are removed and remaining rows shift down. Empty rows added at top
     * to maintain board dimensions.</p>
     *
     * <p>Score bonus calculation: 50 × (lines cleared)²
     * Examples: 1=50, 2=200, 3=450, 4=800 (Tetris)</p>
     *
     * @param matrix board matrix to check
     * @return ClearRow with cleared line count, new board state, and score bonus
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates deep copy of a list of 2D matrices.
     *
     * <p>Each matrix in list is independently copied using copy() method.</p>
     *
     * @param list list of matrices to copy
     * @return new list containing copies of all matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
