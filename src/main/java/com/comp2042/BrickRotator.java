package com.comp2042;

import com.comp2042.logic.bricks.Brick;

public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    // Get next shape rotating CLOCKWISE (right)
    public NextShapeInfo getNextShapeClockwise() {
        int nextShape = (currentShape + 1) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    // Get next shape rotating COUNTER-CLOCKWISE (left)
    public NextShapeInfo getNextShapeCounterClockwise() {
        int nextShape = (currentShape - 1 + brick.getShapeMatrix().size()) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    // Get current Rotation State
    public int getCurrentState() {
        return currentShape;
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
}
