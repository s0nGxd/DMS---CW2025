package com.comp2042.logic;

import com.comp2042.data.NextShapeInfo;
import com.comp2042.logic.bricks.Brick;

/**
 * Manages brick rotation state and transitions between rotation states.
 *
 * <p>Handles rotation logic for Tetris pieces, maintaining current rotation
 * state (0-3) and providing methods to get the next rotation state in either
 * clockwise or counter-clockwise direction. Works with the Brick interface
 * to retrieve shape matrices for each rotation state.</p>
 *
 * <p>This class separates rotation state management from the board logic,
 * following the Single Responsibility Principle.</p>
 */

public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Gets the next rotation state when rotating clockwise.
     * <p>Increments rotation state by 1, wrapping from 3 to 0. Returns
     * NextShapeInfo containing the rotated shape matrix and new state index.</p>
     * @return NextShapeInfo with clockwise rotated shape and position
     */
    // Get next shape rotating CLOCKWISE (right)
    public NextShapeInfo getNextShapeClockwise() {
        int nextShape = (currentShape + 1) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Gets the next rotation state when rotating counter-clockwise.
     * <p>Decrements rotation state by 1, wrapping from 0 to 3. Returns
     * NextShapeInfo containing the rotated shape matrix and new state index.</p>
     * @return NextShapeInfo with counter-clockwise rotated shape and position
     */
    // Get next shape rotating COUNTER-CLOCKWISE (left)
    public NextShapeInfo getNextShapeCounterClockwise() {
        int nextShape = (currentShape - 1 + brick.getShapeMatrix().size()) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Returns the current rotation state's shape matrix.
     * @return 2D array representing current brick shape
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }


    /**
     * Gets the current rotation state index.
     * @return rotation state (0-3)
     */
    // Get current Rotation State
    public int getCurrentState() {
        return currentShape;
    }

    /**
     * Returns reference to the current brick being rotated.
     * @return current Brick object
     */
    public Brick getCurrentBrick() {
        return brick;
    }

    /**
     * Sets the rotation state index.
     * @param currentShape new rotation state (0-3)
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets a new brick to rotate and resets rotation state to 0.
     * @param brick the new brick to manage
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
}
