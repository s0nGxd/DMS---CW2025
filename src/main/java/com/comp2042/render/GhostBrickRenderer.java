package com.comp2042.render;

import com.comp2042.data.ViewData;
import com.comp2042.view.ColourMapper;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import static com.comp2042.render.GameConstants.*;

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

                    if (boardRow >= VISIBLE_ROWS_START && boardRow < displayMatrix.length &&
                            boardCol >= 0 && boardCol < displayMatrix[0].length) {

                        Paint baseColor = colourMapper.getFillColor(brickData[i][j]);

                        if (baseColor instanceof Color) {
                            Color color = (Color) baseColor;
                            Color ghostColor = new Color(
                                    color.getRed(),
                                    color.getGreen(),
                                    color.getBlue(),
                                    GHOST_OPACITY
                            );

                            displayMatrix[boardRow][boardCol].setFill(ghostColor);
                            displayMatrix[boardRow][boardCol].setArcHeight(BRICK_ARC_SIZE);
                            displayMatrix[boardRow][boardCol].setArcWidth(BRICK_ARC_SIZE);
                        }
                    }
                }
            }
        }
    }
}