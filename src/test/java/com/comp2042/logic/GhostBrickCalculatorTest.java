package com.comp2042.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

class GhostBrickCalculatorTest {

    @Test
    @DisplayName("Ghost piece should fall to the bottom")
    void testGhostPositionCalculation() {
        GhostBrickCalculator calculator = new GhostBrickCalculator();

        // Setup a 5x5 empty board for testing
        int[][] emptyBoard = new int[5][5];

        // Simple 1x1 block as the shape
        int[][] shape = {{1}};

        // Start at top (0,0)
        Point startPos = new Point(0, 0);

        // In a 5x5 board, a 1x1 block at x=0 should fall to y=4 (index 4 is bottom)
        // However, the calculator checks intersection.
        // If board is empty, it should land at 4 (collision at 5).
        // Depending on your loop implementation:
        // if check is `testY = ghostY + 1`, loop stops when `intersect` is true.

        // NOTE: intersect checks bounds. y=4 is valid. y=5 is out of bounds.
        // So it should return 4.

        int ghostY = calculator.calculateGhostPosition(emptyBoard, shape, startPos);

        assertEquals(4, ghostY, "Ghost piece should be at the bottom row");
    }

    @Test
    @DisplayName("Ghost piece should land on existing blocks")
    void testGhostPositionWithObstacle() {
        GhostBrickCalculator calculator = new GhostBrickCalculator();
        int[][] board = new int[10][10];

        // Place an obstacle at row 8 (0-indexed)
        board[8][0] = 1;

        int[][] shape = {{1}};
        Point startPos = new Point(0, 0);

        // Should land at row 7 (on top of 8)
        int ghostY = calculator.calculateGhostPosition(board, shape, startPos);

        assertEquals(7, ghostY, "Ghost piece should land directly on top of the obstacle");
    }
}