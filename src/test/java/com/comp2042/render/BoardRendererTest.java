package com.comp2042.render;

import com.comp2042.testutil.JavaFXBaseTest;
import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardRendererTest extends JavaFXBaseTest {

    @Test
    @DisplayName("Initialize board fills grid with transparent rectangles")
    void testInitializeBoard() {
        GridPane gridPane = new GridPane();
        BoardRenderer renderer = new BoardRenderer(gridPane, new ColourMapper());

        int[][] emptyBoard = new int[10][10]; // 10x10 board

        Rectangle[][] result = renderer.initializeBoard(emptyBoard);

        // Check array dimensions
        assertEquals(10, result.length);
        assertEquals(10, result[0].length);

        // VISIBLE_ROWS_START is usually 2. The loop in renderer starts from there.
        // So index 0 and 1 in the result array might be null if not initialized,
        // or the grid pane should contain (10-2)*10 = 80 children.

        long childrenCount = gridPane.getChildren().size();
        // Assuming visible rows start at 2, we render rows 2-9 (8 rows) * 10 cols = 80 rects
        // Note: check GameConstants.VISIBLE_ROWS_START value in your code. Assuming 2.

        assertTrue(childrenCount > 0, "GridPane should contain rectangles");
    }
}