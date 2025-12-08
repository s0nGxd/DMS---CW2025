package com.comp2042.render;

import com.comp2042.constant.GameConstants;
import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Renders the game board grid and updates its visual state.
 * Manages the display matrix of rectangles representing the board.
 */

public class BoardRenderer {

    private final GridPane gamePanel;
    private final ColourMapper colourMapper;
    private Rectangle[][] displayMatrix;

    /**
     * Constructs a BoardRenderer.
     * @param gamePanel the GridPane to render into
     * @param colourMapper the color mapping utility
     */
    public BoardRenderer(GridPane gamePanel, ColourMapper colourMapper) {
        this.gamePanel = gamePanel;
        this.colourMapper = colourMapper;
    }

    /**
     * Initializes the board display with rectangles.
     * @param boardMatrix the initial board state
     * @return 2D array of rectangles representing the board
     */
    public Rectangle[][] initializeBoard(int[][] boardMatrix) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        for (int i = GameConstants.VISIBLE_ROWS_START; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - GameConstants.VISIBLE_ROWS_START);
            }
        }

        return displayMatrix;
    }

    /**
     * Refreshes the board display to match the current state.
     * @param board the current board matrix
     */
    public void refreshBoard(int[][] board) {
        for (int i = GameConstants.VISIBLE_ROWS_START; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Set the display matrix of rectangles.
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(colourMapper.getFillColor(color));
        rectangle.setArcHeight(GameConstants.BRICK_ARC_SIZE);
        rectangle.setArcWidth(GameConstants.BRICK_ARC_SIZE);
    }
}