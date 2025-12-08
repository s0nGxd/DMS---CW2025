package com.comp2042.render;

import com.comp2042.constant.GameConstants;
import com.comp2042.data.ViewData;
import com.comp2042.testutil.JavaFXBaseTest;
import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickPanelRendererTest extends JavaFXBaseTest {

    private GridPane gridPane;
    private BrickPanelRenderer renderer;

    @BeforeEach
    void setUp() {
        gridPane = new GridPane();
        renderer = new BrickPanelRenderer(gridPane, new ColourMapper());
    }

    @Test
    @DisplayName("Rendering a brick adds rectangles to the grid")
    void testRenderBrick() {
        // Create a 2x2 brick at position (5, 5)
        int[][] brickData = {
                {1, 0},
                {0, 1}
        };
        ViewData viewData = new ViewData(brickData, 5, 5, new int[0][0], 0, null, null);

        // Execute render
        renderer.renderBrick(viewData);

        // We expect 2 rectangles (for the non-zero blocks)
        // Note: The logic in renderer filters out 0s.
        // It might add them to children list.

        // Filter children to count Rectangles
        long rectCount = gridPane.getChildren().stream()
                .filter(node -> node instanceof Rectangle)
                .count();

        assertEquals(2, rectCount, "Should render exactly 2 rectangles for the given shape");
    }
}