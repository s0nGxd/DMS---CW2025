package com.comp2042.model;

import com.comp2042.events.GameMode;
import com.comp2042.data.Score;
import com.comp2042.logic.MatrixOperations;
import com.comp2042.data.NextShapeInfo;
import com.comp2042.data.ViewData;
import com.comp2042.data.ClearRow;
import com.comp2042.logic.BrickRotator;
import com.comp2042.logic.SRSKickData;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick heldBrick = null;
    private boolean heldThisTurn = false;

    private GameMode gameMode = GameMode.ZEN;
    private int linesCleared = 0;  // For SPRINT
    private long gameStartTime = 0;  // For BLITZ
    private int currentLevel = 1;  // For PITFALL
    private int fallSpeed = 400;  // For PITFALL (milliseconds)

    private long completionTime = 0;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        gameStartTime = System.currentTimeMillis();
    }

    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getFallSpeed() {
        return fallSpeed;
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

    @Override
    public boolean holdCurrentBrick() {
        if (heldThisTurn) {
            return false;
        }

        Brick currentBrick = brickRotator.getCurrentBrick();

        if (heldBrick == null) {
            // First hold
            heldBrick = currentBrick;
            Brick newBrick = brickGenerator.getBrick();
            brickRotator.setBrick(newBrick);
            currentOffset = new Point(3, 0);
        } else {
            // Swap hold
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(3, 0);
        }

        heldThisTurn = true;
        return true;
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

        // Reset hold flag when new brick spawns
        heldThisTurn = false;

        // Initialize game start time on first brick
        if (gameStartTime == 0) {
            gameStartTime = System.currentTimeMillis();
        }

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
        int[][] heldBrickData = (heldBrick != null)
                ? heldBrick.getShapeMatrix().get(0)  // Always show first rotation
                : null;
        // Get next 4 bricks data
        List<int[][]> nextBricksData = new ArrayList<>();
        List<Brick> nextBricks = brickGenerator.getNextBricks(4);
        for (Brick brick : nextBricks) {
            nextBricksData.add(brick.getShapeMatrix().get(0));
        }
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0), ghostY, heldBrickData, nextBricksData);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();

        // Track lines cleared for game modes
        linesCleared += clearRow.getLinesRemoved();

        // PITFALL: Increase level every 10 lines
        if (gameMode == GameMode.PITFALL && linesCleared / 10 > currentLevel - 1) {
            currentLevel = linesCleared / 10 + 1;
            fallSpeed = Math.max(100, 400 - (currentLevel - 1) * 30); // Speed up
        }

        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long time) {
        this.completionTime = time;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        // Resets the hold brick function
        heldBrick = null;

        // Reset game mode variables
        linesCleared = 0;
        gameStartTime = 0;
        currentLevel = 1;
        fallSpeed = 400;

        createNewBrick();
    }
}
