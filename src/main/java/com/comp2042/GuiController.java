package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private BorderPane gameBoard;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private int[][] currentBoardMatrix;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private Stage stage;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    public void setStage(Stage stage){
        this.stage = stage;

        // Listen for Fullscreen toggle
        stage.fullScreenProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(this::updateLayout));

        // Listen for maximize/unmaximize
        stage.maximizedProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(this::updateLayout));

        // Listen for window width changes
        stage.widthProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(this::updateLayout));

        // Listen for window height changes
        stage.heightProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(this::updateLayout));

        // Center immediately when stage is first set
        Platform.runLater(this::updateLayout);
    }

    private void centerNoti() {
        double centerX = gameBoard.getLayoutX() + (gameBoard.getWidth() / 2);
        double centerY = gameBoard.getLayoutY() + (gameBoard.getHeight() / 2);

        // Use the bounds of the group's content
        double notificationWidth = groupNotification.getBoundsInParent().getWidth();
        double notificationHeight = groupNotification.getBoundsInParent().getHeight();

        groupNotification.setLayoutX(centerX - notificationWidth / 2);
        groupNotification.setLayoutY(centerY - notificationHeight / 2);
    }

    private void updateLayout() {
        // Center the Gameboard
        gameBoard.setLayoutX((stage.getWidth() - gameBoard.getWidth()) / 2);
        gameBoard.setLayoutY((stage.getHeight() - gameBoard.getHeight()) / 2);

        // Update brick position to match new gameBoard position
        if (eventListener != null) {
            ViewData currentViewData = eventListener.getCurrentViewData();
                    refreshBrick(currentViewData);
        }

        centerNoti();
    }

    private void toggleFullScreen() {
        if (stage != null) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    // Added a Key for Clockwise Rotation (to add another for counter-clockwise)
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W || keyEvent.getCode() == KeyCode.Z) {
                        refreshBrick(eventListener.onRotateLeftEvent(new MoveEvent(EventType.ROTATE_LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.X) {
                        refreshBrick(eventListener.onRotateRightEvent(new MoveEvent(EventType.ROTATE_RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    // ADDING A HARD DROP FUNCTION if SPACE input
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        dropDown(new MoveEvent(EventType.DROP, EventSource.USER));
                        keyEvent.consume();
                    }
                }

                if (keyEvent.getCode() == KeyCode.F11) {
                    toggleFullScreen();
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        this.currentBoardMatrix = boardMatrix;

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gameBoard.getLayoutX() + gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(gameBoard.getLayoutY() + gamePanel.getLayoutY() - 42 + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Clears old Ghost Brick
            if (currentBoardMatrix != null) {
                for (int i = 2; i < currentBoardMatrix.length; i++) {
                    for (int j = 0; j < currentBoardMatrix[i].length; j++) {
                        setRectangleData(currentBoardMatrix[i][j], displayMatrix[i][j]);
                    }
                }
            }

            // Render new Ghost Brick
            renderGhostBrick(brick);

            brickPanel.setLayoutX(gameBoard.getLayoutX() + gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(gameBoard.getLayoutY() + gamePanel.getLayoutY() - 42 + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        this.currentBoardMatrix = board;
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    // Added Function to Instant Drop to the Bottom
    private void dropDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData dropData = eventListener.onDropEvent(event);
            if (dropData.getClearRow() != null && dropData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + dropData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(dropData.getViewData());
        }
        gamePanel.requestFocus();
    }

    // Render the ghost brick together with other bricks (shows where the current brick will land)
    private void renderGhostBrick(ViewData brick) {
        // Don't show ghost if brick is already landed
        if (brick.getyPosition() == brick.getGhostPosition()) {
            return;
        }

        // Get ghost position and brick data
        int ghostY = brick.getGhostPosition();
        int ghostX = brick.getxPosition();
        int[][] brickData = brick.getBrickData();

        // Draw ghost onto the game panel
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int boardRow = ghostY + i;
                    int boardCol = ghostX + j;

                    // Make sure its within bounds and within visible area (row 2+)
                    if (boardRow >= 2 && boardRow < displayMatrix.length &&
                            boardCol >= 0 && boardCol < displayMatrix[0].length) {

                        // Get the color of the brick
                        Paint baseColor = getFillColor(brickData[i][j]);

                        // Create semi-transparent version
                        if (baseColor instanceof Color) {
                            Color color = (Color) baseColor;
                            Color ghostColor = new Color(
                                    color.getRed(),
                                    color.getGreen(),
                                    color.getBlue(),
                                    0.3  // 30% opacity
                            );

                            // Set the ghost color on the display matrix
                            displayMatrix[boardRow][boardCol].setFill(ghostColor);
                            displayMatrix[boardRow][boardCol].setArcHeight(9);
                            displayMatrix[boardRow][boardCol].setArcWidth(9);
                        }
                    }
                }
            }
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
