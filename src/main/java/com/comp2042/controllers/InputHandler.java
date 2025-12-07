package com.comp2042.controllers;

import com.comp2042.data.ViewData;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import javafx.beans.property.BooleanProperty;
import javafx.scene.input.KeyCode;

import java.util.function.Consumer;

public class InputHandler {

    private InputEventListener eventListener;
    private final BooleanProperty isPause;
    private final BooleanProperty isGameOver;

    public InputHandler(BooleanProperty isPause, BooleanProperty isGameOver) {
        this.isPause = isPause;
        this.isGameOver = isGameOver;
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void handleKeyPress(KeyCode code,
                               Runnable returnToMenu,
                               Runnable newGame,
                               Consumer<ViewData> refreshBrick,
                               Consumer<MoveEvent> moveDown,
                               Consumer<MoveEvent> dropDown) {
        // KEYS THAT NEEDS TO WORK OUTSIDE GAMEPLAY
        //ESCAPE KEY
        if (code == KeyCode.ESCAPE) {
            returnToMenu.run();
            return;
        }

        // N KEY FOR NEW GAME
        if (code == KeyCode.N) {
            newGame.run();
            return;
        }

        // KEYS THAT ONLY WORK DURING GAMEPLAY
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (code == KeyCode.LEFT || code == KeyCode.A) {
                refreshBrick.accept(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
            }
            if (code == KeyCode.RIGHT || code == KeyCode.D) {
                refreshBrick.accept(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
            }
            // Added a Key for Clockwise Rotation (to add another for counter-clockwise)
            if (code == KeyCode.UP || code == KeyCode.W || code == KeyCode.Z) {
                refreshBrick.accept(eventListener.onRotateLeftEvent(new MoveEvent(EventType.ROTATE_LEFT, EventSource.USER)));
            }
            if (code == KeyCode.X) {
                refreshBrick.accept(eventListener.onRotateRightEvent(new MoveEvent(EventType.ROTATE_RIGHT, EventSource.USER)));
            }
            if (code == KeyCode.DOWN || code == KeyCode.S) {
                moveDown.accept(new MoveEvent(EventType.DOWN, EventSource.USER));
            }
            // ADDING A HARD DROP FUNCTION if SPACE input
            if (code == KeyCode.SPACE) {
                dropDown.accept(new MoveEvent(EventType.DROP, EventSource.USER));
            }

            // ADDED A HOLD FUNCTION
            if (code == KeyCode.C) {
                refreshBrick.accept(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
            }
        }
    }
}