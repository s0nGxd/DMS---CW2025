package com.comp2042.logic;

import java.awt.Point;


public class GhostBrickCalculator {

    public int calculateGhostPosition(int[][] currentGameMatrix,
                                      int[][] currentShape,
                                      Point currentOffset) {
        // Start from current Y position
        int ghostY = (int) currentOffset.getY();
        int currentX = (int) currentOffset.getX();

        // Create a copy of the game matrix to test collisions
        int[][] matrixCopy = MatrixOperations.copy(currentGameMatrix);

        // Keep moving down until collision is detected
        while (true) {
            int testY = ghostY + 1;

            // Check if next position down would cause collision
            boolean collision = MatrixOperations.intersect(
                    matrixCopy,
                    currentShape,
                    currentX,
                    testY
            );

            if (collision) {
                // Can't go further down, current ghostY is the landing position
                return ghostY;
            }

            // Can go further down
            ghostY = testY;
        }
    }


    public int calculateGhostPosition(int[][] currentGameMatrix,
                                      BrickRotator rotator,
                                      Point currentOffset) {
        return calculateGhostPosition(
                currentGameMatrix,
                rotator.getCurrentShape(),
                currentOffset
        );
    }
}