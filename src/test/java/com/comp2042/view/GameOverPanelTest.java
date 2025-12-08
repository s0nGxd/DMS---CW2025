package com.comp2042.view;

import com.comp2042.testutil.JavaFXBaseTest;
import javafx.scene.control.Label;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameOverPanelTest extends JavaFXBaseTest {

    @Test
    @DisplayName("GameOverPanel initializes with correct Label")
    void testGameOverPanelContent() {
        GameOverPanel panel = new GameOverPanel();

        // It extends BorderPane, check the center node
        assertNotNull(panel.getCenter(), "Center content should not be null");
        assertTrue(panel.getCenter() instanceof Label, "Center content should be a Label");

        Label label = (Label) panel.getCenter();
        assertEquals("GAME OVER", label.getText(), "Label should display 'GAME OVER'");
        assertTrue(label.getStyleClass().contains("gameOverStyle"), "Label should have correct CSS style");
    }
}