package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    private int getBrickType() {
        int[][] shape = brickRotator.getCurrentShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    return shape[i][j];
                }
            }
        }
        return 0;
    }

    private boolean SRSRotation(NextShapeInfo nextShape) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int currentState = brickRotator.getCurrentState();
        int targetState = nextShape.getPosition();
        int brickType = getBrickType();

        // Get the official SRS kick offsets for rotation
        Point[] kickOffsets = SRSKickData.getKickOffsets(brickType, currentState, targetState);

        // Try each kick offset in order until one succeed
        for (Point kick : kickOffsets) {
            int testX = (int) currentOffset.getX() + kick.x;
            int testY = (int) currentOffset.getY() - kick.y;

            if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), testX, testY)) {
                // Determine successful rotation with this kick
                currentOffset.setLocation(testX, testY);
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }
        // All kick attempts failed
        return false;
    }

    @Override
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShapeCounterClockwise();
        return SRSRotation(nextShape);
    }

    @Override
    public boolean rotateRightBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShapeClockwise();
        return SRSRotation(nextShape);
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(3, 0);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }


     //Calculates the Y position for Ghost Brick
    private int calculateGhostPosition() {
        int ghostY = (int) currentOffset.getY();
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int currentX = (int) currentOffset.getX();
        int[][] currentShape = brickRotator.getCurrentShape();

        // Keep moving down until it hit something
        while (true) {
            int testY = ghostY + 1;

            // Check if next position down would cause collision
            boolean collision = MatrixOperations.intersect(
                    currentMatrix,
                    currentShape,
                    currentX,
                    testY
            );

            if (collision) {
                // Can't go further down, ghostY = landing position
                return ghostY;
            }

            // Can go further down
            ghostY = testY;
        }
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        int ghostY = calculateGhostPosition();
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0), ghostY);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}
