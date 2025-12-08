package com.comp2042.controllers;

import com.comp2042.data.ViewData;
import com.comp2042.events.EventType;
import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {

    private InputHandler inputHandler;
    private BooleanProperty isPause;
    private BooleanProperty isGameOver;
    private MockEventListener eventListener;

    // Simple Mock to verify callbacks
    private static class MockEventListener implements InputEventListener {
        EventType lastEventType;

        @Override
        public ViewData onLeftEvent(MoveEvent event) { lastEventType = event.getEventType(); return null; }
        @Override
        public ViewData onRightEvent(MoveEvent event) { lastEventType = event.getEventType(); return null; }
        @Override
        public ViewData onRotateLeftEvent(MoveEvent event) { lastEventType = event.getEventType(); return null; }
        @Override
        public ViewData onRotateRightEvent(MoveEvent event) { lastEventType = event.getEventType(); return null; }
        @Override
        public ViewData onHoldEvent(MoveEvent event) { lastEventType = event.getEventType(); return null; }
        @Override
        public com.comp2042.data.DownData onDownEvent(MoveEvent event) { lastEventType = event.getEventType(); return null; }
        @Override
        public com.comp2042.data.DownData onDropEvent(MoveEvent event) { lastEventType = event.getEventType(); return null; }
        @Override
        public ViewData getCurrentViewData() { return null; }
        @Override
        public void createNewGame() {}
    }

    @BeforeEach
    void setUp() {
        isPause = new SimpleBooleanProperty(false);
        isGameOver = new SimpleBooleanProperty(false);
        inputHandler = new InputHandler(isPause, isGameOver);

        eventListener = new MockEventListener();
        inputHandler.setEventListener(eventListener);
    }

    @Test
    @DisplayName("ESC key should trigger return to menu")
    void testEscKey() {
        AtomicBoolean menuCalled = new AtomicBoolean(false);

        inputHandler.handleKeyPress(KeyCode.ESCAPE,
                () -> menuCalled.set(true),
                () -> {}, null, null, null);

        assertTrue(menuCalled.get(), "ESC should call returnToMenu runnable");
    }

    @Test
    @DisplayName("Input ignored when Game Over")
    void testGameOverPreventsInput() {
        isGameOver.set(true);

        inputHandler.handleKeyPress(KeyCode.LEFT,
                () -> {}, () -> {},
                (v) -> {}, (m) -> {}, (m) -> {});

        assertNull(eventListener.lastEventType, "No event should fire if Game Over is true");
    }

    @Test
    @DisplayName("Space bar triggers DROP event")
    void testHardDrop() {
        AtomicBoolean dropCalled = new AtomicBoolean(false);

        inputHandler.handleKeyPress(KeyCode.SPACE,
                () -> {}, () -> {}, (v) -> {},
                (m) -> {},
                (m) -> {
                    assertEquals(EventType.DROP, m.getEventType());
                    dropCalled.set(true);
                });

        assertTrue(dropCalled.get());
    }
}