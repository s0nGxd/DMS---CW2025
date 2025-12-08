package com.comp2042.events;

import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;

/**
 * Interface for handling player input and game events.
 * Defines callbacks for all possible player actions and game state queries.
 */

public interface InputEventListener {

    /**
     * Handles downward movement events for the active brick.
     * @param event the movement event containing source information
     * @return DownData containing clear row information and updated view state
     */
    DownData onDownEvent(MoveEvent event);
    /**
     * Handles leftward movement events for the active brick.
     * @param event the movement event containing source information
     * @return ViewData containing the updated brick position and state
     */
    ViewData onLeftEvent(MoveEvent event);
    /**
     * Handles rightward movement events for the active brick.
     * @param event the movement event containing source information
     * @return ViewData containing the updated brick position and state
     */
    ViewData onRightEvent(MoveEvent event);
    /**
     * Handles counter-clockwise rotation events for the active brick.
     * @param event the movement event containing source information
     * @return ViewData containing the updated brick rotation and state
     */
    ViewData onRotateLeftEvent(MoveEvent event);
    /**
     * Handles clockwise rotation events for the active brick.
     * @param event the movement event containing source information
     * @return ViewData containing the updated brick rotation and state
     */
    ViewData onRotateRightEvent(MoveEvent event);
    /**
     * Handles hard drop events, instantly dropping the brick to the bottom.
     * @param event the movement event containing source information
     * @return DownData containing clear row information and updated view state
     */
    DownData onDropEvent (MoveEvent event);
    /**
     * Handles hold events, allowing the player to swap the current brick with held brick.
     * @param event the movement event containing source information
     * @return ViewData containing the updated view state after holding
     */
    ViewData onHoldEvent (MoveEvent event);
    /**
     * Gets the current view state of the game.
     * @return ViewData representing the current brick position and game state
     */
    ViewData getCurrentViewData();
    /**
     * Initializes a new game, resetting all game state.
     */
    void createNewGame();
}
