package com.comp2042.view;

import com.comp2042.data.ViewData;
import com.comp2042.view.ColourMapper;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class GhostBrickRenderer {

    private final Rectangle[][] displayMatrix;
    private final ColourMapper colourMapper;

    public GhostBrickRenderer(Rectangle[][] displayMatrix, ColourMapper colourMapper) {
        this.displayMatrix = displayMatrix;
        this.colourMapper = colourMapper;
    }

    // Render the ghost brick together with other bricks (shows where the current brick will land)
    public void renderGhostBrick(ViewData brick) {
        // Don't show ghost if brick is already landed
        if (brick.getyPosition() == brick.getGhostPosition()) {
            return;
        }

        int ghostY = brick.getGhostPosition();
        int ghostX = brick.getxPosition();
        int[][] brickData = brick.getBrickData();

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int boardRow = ghostY + i;
                    int boardCol = ghostX + j;

                    // Make sure its within bounds and within visible area (row 2+)
                    if (boardRow >= 2 && boardRow < displayMatrix.length &&
                            boardCol >= 0 && boardCol < displayMatrix[0].length) {

                        Paint baseColor = colourMapper.getFillColor(brickData[i][j]);

                        if (baseColor instanceof Color) {
                            Color color = (Color) baseColor;
                            Color ghostColor = new Color(
                                    color.getRed(),
                                    color.getGreen(),
                                    color.getBlue(),
                                    0.3  // 30% opacity
                            );

                            displayMatrix[boardRow][boardCol].setFill(ghostColor);
                            displayMatrix[boardRow][boardCol].setArcHeight(9);
                            displayMatrix[boardRow][boardCol].setArcWidth(9);
                        }
                    }
                }
            }
        }
    }
}