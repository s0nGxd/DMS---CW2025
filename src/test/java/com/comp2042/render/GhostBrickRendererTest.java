package com.comp2042.render;

import com.comp2042.constant.GameConstants;
import com.comp2042.data.ViewData;
import com.comp2042.testutil.JavaFXBaseTest;
import com.comp2042.view.ColourMapper;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GhostBrickRendererTest extends JavaFXBaseTest {

    private Rectangle[][] displayMatrix;
    private GhostBrickRenderer renderer;
    private static final int ROWS = 20;
    private static final int COLS = 10;

    @BeforeEach
    void setUp() {
        // Initialize a mock board of Rectangles
        displayMatrix = new Rectangle[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                displayMatrix[i][j] = new Rectangle(20, 20);
                displayMatrix[i][j].setFill(Color.TRANSPARENT);
            }
        }
        renderer = new GhostBrickRenderer(displayMatrix, new ColourMapper());
    }

    @Test
    @DisplayName("RenderGhostBrick applies correct opacity to target cells")
    void testRenderGhostBrick() {
        // Create a 1x1 brick
        int[][] brickData = {{1}};
        // Position: x=5, y=5. Ghost Position: y=15 (near bottom)
        ViewData viewData = new ViewData(brickData, 5, 5, new int[0][0], 15, null, null);

        renderer.renderGhostBrick(viewData);

        // The ghost should be rendered at board[15][5]
        // Note: loop offset is ghostY + i. ghostY=15, i=0 => row 15.
        // x=5, j=0 => col 5.

        Rectangle targetRect = displayMatrix[15][5];
        Color fill = (Color) targetRect.getFill();

        // Verify it is not transparent
        assertNotEquals(Color.TRANSPARENT, fill);

        // Verify Opacity is 0.3 (GameConstants.GHOST_OPACITY)
        assertEquals(0.3, fill.getOpacity(), 0.01, "Ghost brick should have 0.3 opacity");
    }

    @Test
    @DisplayName("Ghost brick is not rendered if it overlaps current brick")
    void testGhostOverlapSkip() {
        int[][] brickData = {{1}};
        // Current Y = 15, Ghost Y = 15 (Landed state)
        ViewData viewData = new ViewData(brickData, 5, 15, new int[0][0], 15, null, null);

        renderer.renderGhostBrick(viewData);

        // Should remain Transparent because ghost isn't drawn when landed
        assertEquals(Color.TRANSPARENT, displayMatrix[15][5].getFill());
    }
}