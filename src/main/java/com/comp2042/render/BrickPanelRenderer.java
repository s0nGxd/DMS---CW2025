package com.comp2042.render;

import com.comp2042.constant.GameConstants;
import com.comp2042.data.ViewData;
import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the active falling brick on the game board.
 * Manages the visual representation of the current piece.
 */

public class BrickPanelRenderer {

    private final GridPane gamePanel; // Changed: Target the main game board
    private final ColourMapper colourMapper;
    private final List<Rectangle> activeRectangles = new ArrayList<>(); // New: Track active pieces

    /**
     * Constructs a BrickPanelRenderer.
     * @param gamePanel the GridPane to render into
     * @param colourMapper the color mapping utility
     */
    public BrickPanelRenderer(GridPane gamePanel, ColourMapper colourMapper) {
        this.gamePanel = gamePanel;
        this.colourMapper = colourMapper;
    }

    /**
     * Renders the current brick at its position.
     * @param brick the view data containing brick information
     */
    public void renderBrick(ViewData brick) {
        // 1. Clear old falling piece from the main board
        for (Rectangle rect : activeRectangles) {
            gamePanel.getChildren().remove(rect);
        }
        activeRectangles.clear();

        // 2. Render new position directly into gamePanel grid cells
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int gridX = brick.getxPosition() + j;
                    int gridY = brick.getyPosition() + i;

                    // Only draw visible rows (offset by VISIBLE_ROWS_START)
                    if (gridY >= GameConstants.VISIBLE_ROWS_START) {
                        Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                        setRectangleData(brickData[i][j], rectangle);

                        // Add to grid at exact coordinates
                        gamePanel.add(rectangle, gridX, gridY - GameConstants.VISIBLE_ROWS_START);
                        activeRectangles.add(rectangle);
                    }
                }
            }
        }
    }

    /**
     * Set the brick's colour from colourMapper
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(colourMapper.getFillColor(color));
        rectangle.setArcHeight(GameConstants.BRICK_ARC_SIZE);
        rectangle.setArcWidth(GameConstants.BRICK_ARC_SIZE);
    }
}