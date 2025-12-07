package com.comp2042.view;

import com.comp2042.data.ViewData;
import com.comp2042.events.InputEventListener;
import com.comp2042.render.GameConstants;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UILayoutManager {

    private final Stage stage;
    private final BorderPane gameBoard;
    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final HBox headerArea;
    private final VBox footerArea;
    private final VBox heldBrickBox;
    private final VBox nextBricksBox;
    private final Group groupNotification;
    private InputEventListener eventListener;

    public UILayoutManager(Stage stage, BorderPane gameBoard, GridPane gamePanel, GridPane brickPanel,
                           HBox headerArea, VBox footerArea, VBox heldBrickBox, VBox nextBricksBox,
                           Group groupNotification) {
        this.stage = stage;
        this.gameBoard = gameBoard;
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.headerArea = headerArea;
        this.footerArea = footerArea;
        this.heldBrickBox = heldBrickBox;
        this.nextBricksBox = nextBricksBox;
        this.groupNotification = groupNotification;
    }

    public void updateBrickPosition(ViewData viewData, int brickSize) {
        if (brickPanel != null && viewData != null) {
            brickPanel.setLayoutX(gameBoard.getLayoutX() + gamePanel.getLayoutX() +
                    viewData.getxPosition() * brickPanel.getVgap() +
                    viewData.getxPosition() * brickSize);
            brickPanel.setLayoutY(gameBoard.getLayoutY() + gamePanel.getLayoutY() - 42 +
                    viewData.getyPosition() * brickPanel.getHgap() +
                    viewData.getyPosition() * brickSize);
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void updateBrickPosition(ViewData viewData) {
        if (brickPanel != null && viewData != null) {
            brickPanel.setLayoutX(gameBoard.getLayoutX() + gamePanel.getLayoutX() +
                    viewData.getxPosition() * brickPanel.getVgap() +
                    viewData.getxPosition() * GameConstants.BRICK_SIZE);
            brickPanel.setLayoutY(gameBoard.getLayoutY() + gamePanel.getLayoutY() + GameConstants.BRICK_Y_OFFSET +
                    viewData.getyPosition() * brickPanel.getHgap() +
                    viewData.getyPosition() * GameConstants.BRICK_SIZE);
        }
    }

    public void centerNotification() {
        double centerX = gameBoard.getLayoutX() + (gameBoard.getWidth() / 2);
        double centerY = gameBoard.getLayoutY() + (gameBoard.getHeight() / 2);

        double notificationWidth = groupNotification.getBoundsInParent().getWidth();
        double notificationHeight = groupNotification.getBoundsInParent().getHeight();

        groupNotification.setLayoutX(centerX - notificationWidth / 2);
        groupNotification.setLayoutY(centerY - notificationHeight / 2);
    }

    public void updateLayout() {
        if (stage == null) return;

        double windowWidth = stage.getWidth();
        double windowHeight = stage.getHeight();

        // Calculated total width of game area (side panels + game panel + spacing)
        double heldBrickWidth = heldBrickBox != null ? heldBrickBox.getWidth() : 100;
        double gamePanelWidth = gamePanel.getWidth();
        double nextBrickWidth = nextBricksBox != null ? nextBricksBox.getWidth() : 100;
        double sideSpacing = 20;
        double totalWidth = heldBrickWidth + gamePanelWidth + nextBrickWidth + (2 * sideSpacing);

        // Calculated total height of all elements (header + game panel + footer + spacing)
        double headerHeight = headerArea != null ? headerArea.getHeight() : 60;
        double gamePanelHeight = gamePanel.getHeight();
        double footerHeight = footerArea != null ? footerArea.getHeight() : 80;
        double verticalSpacing = 20;
        double totalHeight = headerHeight + gamePanelHeight + footerHeight + (2 * verticalSpacing);

        // Calculate starting positions to center everything
        double startX = (windowWidth - totalWidth) / 2;
        double startY = (windowHeight - totalHeight) / 2;

        // Position header at the top
        if (headerArea != null) {
            double headerX = startX + (totalWidth - headerArea.getWidth()) / 2;
            headerArea.setLayoutX(headerX);
            headerArea.setLayoutY(startY);
        }

        // Position game board below header
        double gameBoardY = startY + headerHeight + verticalSpacing;
        double gameBoardX = startX + heldBrickWidth + sideSpacing;
        gameBoard.setLayoutX(gameBoardX);
        gameBoard.setLayoutY(gameBoardY);

        // Position held brick panel to the left of game panel
        if (heldBrickBox != null) {
            double holdBoxY = gameBoardY + (gamePanelHeight - heldBrickBox.getHeight()) / 2;
            heldBrickBox.setLayoutX(startX);
            heldBrickBox.setLayoutY(holdBoxY);
        }

        // Position next bricks panel to the right of game panel
        if (nextBricksBox != null) {
            double nextBoxX = startX + heldBrickWidth + gamePanelWidth + (2 * sideSpacing);
            double nextBoxY = gameBoardY + (gamePanelHeight - nextBricksBox.getHeight()) / 2;
            nextBricksBox.setLayoutX(nextBoxX);
            nextBricksBox.setLayoutY(nextBoxY);
        }

        // Position footer below game panel
        if (footerArea != null) {
            double footerX = startX + (totalWidth - footerArea.getWidth()) / 2;
            double footerY = startY + headerHeight + gamePanelHeight + (2 * verticalSpacing);
            footerArea.setLayoutX(footerX);
            footerArea.setLayoutY(footerY);
        }

        // Position active brick panel to match game board
        if (brickPanel != null) {
            brickPanel.setLayoutX(gameBoard.getLayoutX());
            brickPanel.setLayoutY(gameBoard.getLayoutY());
        }
        centerNotification();
    }
}