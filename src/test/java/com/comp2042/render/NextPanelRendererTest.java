package com.comp2042.render;

import com.comp2042.testutil.JavaFXBaseTest;
import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NextPanelRendererTest extends JavaFXBaseTest {

    @Test
    @DisplayName("Render Next Bricks adds grids to VBox")
    void testRenderNextBricks() {
        GridPane holdPane = new GridPane();
        VBox nextBox = new VBox();
        NextPanelRenderer renderer = new NextPanelRenderer(holdPane, nextBox, new ColourMapper());

        List<int[][]> nextPieces = new ArrayList<>();
        nextPieces.add(new int[][]{{1}});
        nextPieces.add(new int[][]{{2}});

        renderer.renderNextBricks(nextPieces);

        assertEquals(2, nextBox.getChildren().size(), "Should add 2 GridPanes to the VBox");
    }
}