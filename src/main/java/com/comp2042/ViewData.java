package com.comp2042;

import java.util.List;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final int ghostPosition;
    private final int[][] heldBrickData;
    private final List<int[][]> nextBricksData;

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, int ghostPosition, int[][] heldBrickData, List<int[][]> nextBricksData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostPosition = ghostPosition;
        this.heldBrickData = heldBrickData;
        this.nextBricksData = nextBricksData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    // Method to get next brick
    public List<int[][]> getNextBricksData() {
        return nextBricksData;
    }

    public int getGhostPosition() {
        return ghostPosition;
    }

    // Saving held brick data for display
    public int[][] getHeldBrickData() {
        return heldBrickData != null ? MatrixOperations.copy(heldBrickData) : null;
    }
}
