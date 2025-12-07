package com.comp2042.render;

import com.comp2042.data.ViewData;
import com.comp2042.view.ColourMapper;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class BrickPanelRenderer {

    private final GridPane brickPanel;
    private final ColourMapper colourMapper;
    private Rectangle[][] rectangles;

    public BrickPanelRenderer(GridPane brickPanel, ColourMapper colourMapper) {
        this.brickPanel = brickPanel;
        this.colourMapper = colourMapper;
    }

    public void renderBrick(ViewData brick) {
        // Clear old rectangles
        brickPanel.getChildren().clear();

        // Recreate rectangles array with correct size
        int brickRows = brick.getBrickData().length;
        int brickCols = brick.getBrickData()[0].length;
        rectangles = new Rectangle[brickRows][brickCols];

        // Add rectangles
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                setRectangleData(brick.getBrickData()[i][j], rectangle);
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(colourMapper.getFillColor(color));
        rectangle.setArcHeight(GameConstants.BRICK_ARC_SIZE);
        rectangle.setArcWidth(GameConstants.BRICK_ARC_SIZE);
    }

    public Rectangle[][] getRectangles() {
        return rectangles;
    }
}