package com.comp2042.model;

import com.comp2042.constant.GameConstants;
import com.comp2042.data.ClearRow;
import com.comp2042.data.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT);
        board.newGame();
    }

    @Test
    @DisplayName("New Game should initialize board and score")
    void testNewGame() {
        assertNotNull(board.getBoardMatrix(), "Board matrix should be initialized");
        assertEquals(0, board.getScore().scoreProperty().get(), "Score should start at 0");
        assertNotNull(board.getViewData(), "ViewData should not be null");
    }

    @Test
    @DisplayName("Brick should move down into empty space")
    void testMoveBrickDown() {
        // Get initial Y position from ViewData
        int startY = board.getViewData().getyPosition();

        boolean moved = board.moveBrickDown();

        assertTrue(moved, "Brick should move down successfully");
        assertEquals(startY + 1, board.getViewData().getyPosition(), "Y position should increment by 1");
    }

    @Test
    @DisplayName("Brick should move left and right")
    void testHorizontalMovement() {
        int startX = board.getViewData().getxPosition();

        board.moveBrickLeft();
        assertEquals(startX - 1, board.getViewData().getxPosition(), "Should move left");

        board.moveBrickRight();
        assertEquals(startX, board.getViewData().getxPosition(), "Should move right (back to start)");
    }

    @Test
    @DisplayName("Hold Brick should swap current brick")
    void testHoldBrick() {
        ViewData initialView = board.getViewData();
        int[][] initialShape = initialView.getBrickData();

        boolean held = board.holdCurrentBrick();

        assertTrue(held, "First hold action should succeed");
        assertNotNull(board.getViewData().getHeldBrickData(), "Held brick data should be present in view");

        // Attempt to hold again immediately (should fail as per rules)
        boolean heldAgain = board.holdCurrentBrick();
        assertFalse(heldAgain, "Should not allow holding twice in one turn");
    }

    @Test
    @DisplayName("Clear Rows should return correct score bonus")
    void testClearRows() {
        // This is a logic test; mocking the matrix state is hard because it's private.
        // However, we can test the behavior of an empty clear.
        ClearRow result = board.clearRows();
        assertEquals(0, result.getLinesRemoved());
        assertEquals(0, result.getScoreBonus());
    }
}