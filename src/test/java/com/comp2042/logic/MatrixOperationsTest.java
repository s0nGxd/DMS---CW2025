package com.comp2042.logic;

import com.comp2042.data.ClearRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {

    @Test
    @DisplayName("Intersect returns true when brick overlaps existing blocks")
    void testIntersectCollision() {
        int[][] board = new int[10][10];
        board[5][5] = 1; // Existing block

        // 1x1 brick at the same location
        int[][] brick = {{1}};

        boolean result = MatrixOperations.intersect(board, brick, 5, 5);
        assertTrue(result, "Should detect collision with existing block");
    }

    @Test
    @DisplayName("Intersect returns true when brick is out of bounds")
    void testIntersectOutOfBounds() {
        int[][] board = new int[10][10];
        int[][] brick = {{1}};

        // Test negative X (left wall)
        assertTrue(MatrixOperations.intersect(board, brick, -1, 0), "Should detect left wall collision");

        // Test X beyond width (right wall)
        assertTrue(MatrixOperations.intersect(board, brick, 10, 0), "Should detect right wall collision");

        // Test Y beyond height (floor)
        assertTrue(MatrixOperations.intersect(board, brick, 0, 10), "Should detect floor collision");
    }

    @Test
    @DisplayName("Merge correctly adds brick to the board")
    void testMerge() {
        int[][] board = new int[5][5];
        int[][] brick = {
                {1, 0},
                {0, 1}
        };

        int[][] newBoard = MatrixOperations.merge(board, brick, 1, 1);

        assertEquals(1, newBoard[1][1], "Brick part 1 should be merged");
        assertEquals(1, newBoard[2][2], "Brick part 2 should be merged");
        assertEquals(0, newBoard[1][2], "Empty space should remain 0");
    }

    @Test
    @DisplayName("CheckRemoving detects and clears full lines")
    void testCheckRemoving() {
        int[][] board = new int[4][4];

        // Fill the bottom row (index 3) completely
        for(int x = 0; x < 4; x++) board[3][x] = 1;
        // Partially fill row 2
        board[2][0] = 1;

        ClearRow result = MatrixOperations.checkRemoving(board);

        assertEquals(1, result.getLinesRemoved(), "Should remove exactly 1 line");

        // Check that the new bottom row (index 3) is now the old row 2 (which had a block at 0)
        assertEquals(1, result.getNewMatrix()[3][0], "Rows should shift down");
        assertEquals(0, result.getNewMatrix()[3][1], "Empty parts of shifted row should be 0");

        // Check top row is empty
        assertEquals(0, result.getNewMatrix()[0][0], "New top row should be empty");
    }
}