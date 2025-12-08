package com.comp2042.render;

import com.comp2042.constant.GameConstants;
import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Renders the held brick and upcoming bricks preview panels.
 * Manages the side panel displays showing future pieces.
 */

public class NextPanelRenderer {

    private final GridPane holdBrickPanel;
    private final VBox nextBricksPanel;
    private final ColourMapper colourMapper;

    /**
     * Constructs a NextPanelRenderer.
     * @param holdBrickPanel the GridPane for held brick
     * @param nextBricksPanel the VBox for next bricks
     * @param colourMapper the color mapping utility
     */
    public NextPanelRenderer(GridPane holdBrickPanel, VBox nextBricksPanel, ColourMapper colourMapper) {
        this.holdBrickPanel = holdBrickPanel;
        this.nextBricksPanel = nextBricksPanel;
        this.colourMapper = colourMapper;
    }

    /**
     * Renders the held brick in its panel.
     * @param heldBrickData the held brick's shape matrix, or null
     */
    public void renderHoldBrick(int[][] heldBrickData) {
        holdBrickPanel.getChildren().clear();

        if (heldBrickData != null) {
            // Calculate the size needed for this brick
            int rows = heldBrickData.length;
            int cols = heldBrickData[0].length;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (heldBrickData[i][j] != 0) {
                        Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                        rectangle.setFill(colourMapper.getFillColor(heldBrickData[i][j]));
                        rectangle.setArcHeight(GameConstants.BRICK_ARC_SIZE);
                        rectangle.setArcWidth(GameConstants.BRICK_ARC_SIZE);
                        holdBrickPanel.add(rectangle, j, i);
                    }
                }
            }
        }
    }

    /**
     * Renders the list of upcoming bricks.
     * @param nextBricksData list of upcoming brick shape matrices
     */
    public void renderNextBricks(List<int[][]> nextBricksData) {
        nextBricksPanel.getChildren().clear();

        if (nextBricksData != null) {
            for (int[][] brickData : nextBricksData) {
                GridPane nextBrickGrid = new GridPane();
                nextBrickGrid.setVgap(1);
                nextBrickGrid.setHgap(1);

                // Calculate the size needed for this brick
                int rows = brickData.length;
                int cols = brickData[0].length;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (brickData[i][j] != 0) {
                            Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                            rectangle.setFill(colourMapper.getFillColor(brickData[i][j]));
                            rectangle.setArcHeight(GameConstants.BRICK_ARC_SIZE);
                            rectangle.setArcWidth(GameConstants.BRICK_ARC_SIZE);
                            nextBrickGrid.add(rectangle, j, i);
                        }
                    }
                }
                nextBricksPanel.getChildren().add(nextBrickGrid);
            }
        }
    }
}