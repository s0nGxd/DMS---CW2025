package com.comp2042.render;

import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardRenderer {

    private final GridPane gamePanel;
    private final ColourMapper colourMapper;
    private Rectangle[][] displayMatrix;

    public BoardRenderer(GridPane gamePanel, ColourMapper colourMapper) {
        this.gamePanel = gamePanel;
        this.colourMapper = colourMapper;
    }

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

    public void refreshBoard(int[][] board) {
        for (int i = GameConstants.VISIBLE_ROWS_START; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(colourMapper.getFillColor(color));
        rectangle.setArcHeight(GameConstants.BRICK_ARC_SIZE);
        rectangle.setArcWidth(GameConstants.BRICK_ARC_SIZE);
    }

    public Rectangle[][] getDisplayMatrix() {
        return displayMatrix;
    }
}