package com.comp2042.controllers;

import com.comp2042.data.DownData;
import com.comp2042.events.GameMode;
import com.comp2042.events.MoveEvent;
import com.comp2042.events.EventType;
import com.comp2042.events.EventSource;
import com.comp2042.events.InputEventListener; // Import added
import com.comp2042.testutil.JavaFXBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

// Extends JavaFXBaseTest to handle JavaFX internals safely
class GameControllerTest extends JavaFXBaseTest {

    private GameController gameController;
    private StubGuiController stubGui;

    // Stub class prevents the real GUI from launching windows during tests
    static class StubGuiController extends GuiController {
        @Override
        public void initGameView(int[][] board, com.comp2042.data.ViewData data) {}
        @Override
        public void refreshGameBackground(int[][] board) {}
        @Override
        public void bindScore(javafx.beans.property.IntegerProperty property) {}
        @Override
        public void displayHighScores(GameMode mode, com.comp2042.data.HighScore manager) {}
        @Override
        public void gameOver() {}

        // ADD THIS METHOD TO FIX THE ERROR
        @Override
        public void setEventListener(InputEventListener eventListener) {
            // Do nothing - prevents NullPointerException in tests
        }
    }

    @BeforeEach
    void setUp() {
        stubGui = new StubGuiController();
        gameController = new GameController(stubGui, GameMode.ZEN);
    }

    @Test
    @DisplayName("Controller initializes board and high score manager correctly")
    void testInit() throws Exception {
        // Public method check
        assertNotNull(gameController.getSimpleBoard(), "Board should be initialized");

        // REFLECTION: Access private 'highScore' field without modifying source code
        Field highScoreField = GameController.class.getDeclaredField("highScore");
        highScoreField.setAccessible(true);
        Object highScoreValue = highScoreField.get(gameController);

        assertNotNull(highScoreValue, "HighScore manager should be initialized internally");
    }

    @Test
    @DisplayName("Drop Event calculates physics and returns view data")
    void testDropEvent() {
        MoveEvent event = new MoveEvent(EventType.DROP, EventSource.USER);
        DownData data = gameController.onDropEvent(event);

        assertNotNull(data, "Drop event should return DownData");
        assertNotNull(data.getViewData(), "DownData should contain ViewData");
        // ClearRow might be null if no lines cleared, so we remove that specific assert or check properly
        // assertNotNull(data.getClearRow()); <--- Removed to be safe
    }

    @Test
    @DisplayName("Left movement triggers board update")
    void testLeftEvent() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        assertNotNull(gameController.onLeftEvent(event));
    }
}