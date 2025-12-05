package com.comp2042.controllers;

import com.comp2042.events.InputEventListener;
import com.comp2042.data.DownData;
import com.comp2042.data.HighScore;
import com.comp2042.data.ViewData;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.GameMode;
import com.comp2042.events.MoveEvent;
import com.comp2042.view.*;
import com.comp2042.model.SimpleBoard;
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
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    private GameMode currentGameMode = null;
    private Stage mainStage;
    private MainMenuController mainMenuController;

    @FXML private Pane rootPane;
    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private BorderPane gameBoard;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private GridPane holdBrickPanel;
    @FXML private VBox nextBricksPanel;
    @FXML private VBox heldBrickBox;
    @FXML private VBox nextBricksBox;
    @FXML private VBox footerArea;
    @FXML private Label scoreValue;
    @FXML private HBox headerArea;
    @FXML private Label progressLabel;

    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private int[][] currentBoardMatrix;
    private Rectangle[][] rectangles;
    private Timeline timeLine;
    private Stage stage;
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private UILayoutManager layoutManager;
    private GameMessage gameMessage;
    private GhostBrickRenderer ghostRenderer;
    private ColourMapper colourMapper;

    public void setStage(Stage stage){
        this.stage = stage;

        layoutManager = new UILayoutManager(stage, gameBoard, gamePanel, brickPanel, headerArea, footerArea, heldBrickBox, nextBricksBox, groupNotification);
        gameMessage = new GameMessage(groupNotification, progressLabel, layoutManager);

        // Listen for Fullscreen toggle
        stage.fullScreenProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> layoutManager.updateLayout()));

        // Listen for maximize/unmaximize
        stage.maximizedProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> layoutManager.updateLayout()));

        // Listen for window width changes
        stage.widthProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> layoutManager.updateLayout()));

        // Listen for window height changes
        stage.heightProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> layoutManager.updateLayout()));

        // Center immediately when stage is first set
        Platform.runLater(() -> layoutManager.updateLayout());
    }
    
    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }

    public void setMainMenuStage(Stage stage, MainMenuController menuController) {
        this.mainStage = stage;
        this.mainMenuController = menuController;
    }

    private void returnToMainMenu() {
        // Stop the timeline
        if (timeLine != null) {
            timeLine.stop();
            timeLine = null;
        }

        // Reset game state
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        // Clear event listener
        eventListener = null;

        // Return to main menu using StageManager
        StageManager stageManager = StageManager.getInstance();
        stageManager.switchScene("mainMenu.fxml", controller -> {
            MainMenuController menuController = (MainMenuController) controller;
            menuController.setStage(stageManager.getPrimaryStage());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (rootPane != null) {
            rootPane.setOpacity(0);
        }

        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        colourMapper = new ColourMapper();
        gameMessage = new GameMessage(groupNotification, progressLabel, layoutManager);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // KEYS THAT NEEDS TO WORK OUTSIDE GAMEPLAY
                //ESCAPE KEY
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    returnToMainMenu();
                    keyEvent.consume();
                    return;
                }

                // N KEY FOR NEW GAME
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                    keyEvent.consume();
                    return;
                }

                // KEYS THAT ONLY WORK DURING GAMEPLAY
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

                    // ADDED A HOLD FUNCTION
                    if (keyEvent.getCode() == KeyCode.C) {
                        refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                        keyEvent.consume();
                    }
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

        // Initialize the background grid
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        // Initialize ghost brick
        ghostRenderer = new GhostBrickRenderer(displayMatrix, colourMapper);

        // Show rootPane
        Platform.runLater(() -> {
            if (layoutManager != null) {
                layoutManager.updateLayout();
                refreshBrick(brick);

                // One line to reveal the entire UI in the correct position
                if (rootPane != null) {
                    rootPane.setOpacity(1);
                }
            }
        });

        // Update Game Mode UI (Score/Time labels)
        updateGameModeUI();

        // Setup and Start Game Loop
        int speed = 400;
        if (eventListener instanceof GameController) {
            GameController gameController = (GameController) eventListener;
            SimpleBoard simpleBoard = gameController.getSimpleBoard();
            if (simpleBoard != null) {
                speed = simpleBoard.getFallSpeed();
            }
        }

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(speed),
                ae -> {
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
                    updateGameModeUI();
                }
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private void updateGameModeUI() {
        if (!(eventListener instanceof GameController)) return;

        GameController gameController = (GameController) eventListener;
        SimpleBoard simpleBoard = gameController.getSimpleBoard();

        if (simpleBoard == null || progressLabel == null) return;

        switch (simpleBoard.getGameMode()) {
            case SPRINT:
                int remaining = 40 - simpleBoard.getLinesCleared();
                progressLabel.setText("Lines: " + remaining);
                break;
            case BLITZ:
                long elapsed = System.currentTimeMillis() - simpleBoard.getGameStartTime();
                // Prevent negative time display on first frame
                if (elapsed < 0) elapsed = 0;
                int seconds = (int) ((180000 - elapsed) / 1000);
                progressLabel.setText("Time: " + seconds + "s");
                break;
            case PITFALL:
                progressLabel.setText("Level: " + simpleBoard.getCurrentLevel());
                // Update speed
                if (timeLine != null) {
                    timeLine.setRate(400.0 / simpleBoard.getFallSpeed());
                }
                break;
            case ZEN:
                progressLabel.setText("Zen Mode");
                break;
        }
    }


    private void initHoldBrickPanel(ViewData viewData) {
        holdBrickPanel.getChildren().clear();
        int[][] heldBrickData = viewData.getHeldBrickData();

        if (heldBrickData != null) {
            // Calculate the size needed for this brick
            int rows = heldBrickData.length;
            int cols = heldBrickData[0].length;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (heldBrickData[i][j] != 0) {
                        Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                        rectangle.setFill(colourMapper.getFillColor(heldBrickData[i][j]));
                        rectangle.setArcHeight(9);
                        rectangle.setArcWidth(9);
                        holdBrickPanel.add(rectangle, j, i);
                    }
                }
            }
        }
    }


    private void initNextBricksPanel(ViewData viewData) {
        nextBricksPanel.getChildren().clear();
        List<int[][]> nextBricksData = viewData.getNextBricksData();

        if (nextBricksData != null) {
            for (int[][] brickData : nextBricksData) {
                GridPane nextBrickGrid = new GridPane();
                nextBrickGrid.setVgap(1);
                nextBrickGrid.setHgap(1);

                // Calculate the size needed for the brick
                int rows = brickData.length;
                int cols = brickData[0].length;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (brickData[i][j] != 0) {
                            Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                            rectangle.setFill(colourMapper.getFillColor(brickData[i][j]));
                            rectangle.setArcHeight(9);
                            rectangle.setArcWidth(9);
                            nextBrickGrid.add(rectangle, j, i);
                        }
                    }
                }
                nextBricksPanel.getChildren().add(nextBrickGrid);
            }
        }
    }

    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Clear old rectangles
            brickPanel.getChildren().clear();

            // Recreate rectangles array with correct size
            int brickRows = brick.getBrickData().length;
            int brickCols = brick.getBrickData()[0].length;
            rectangles = new Rectangle[brickRows][brickCols];

            // Clears old Ghost Brick
            if (currentBoardMatrix != null) {
                for (int i = 2; i < currentBoardMatrix.length; i++) {
                    for (int j = 0; j < currentBoardMatrix[i].length; j++) {
                        setRectangleData(currentBoardMatrix[i][j], displayMatrix[i][j]);
                    }
                }
            }

            // Render new Ghost Brick
            ghostRenderer.renderGhostBrick(brick);

            if (layoutManager != null) {
                layoutManager.updateBrickPosition(brick, BRICK_SIZE);
            }

            // Add rectangles
            for (int i = 0; i < brickRows; i++) {
                for (int j = 0; j < brickCols; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    setRectangleData(brick.getBrickData()[i][j], rectangle);
                    rectangles[i][j] = rectangle;
                    brickPanel.add(rectangle, j, i);
                }
            }
            // Update Hold & Next Bricks Panel
            initHoldBrickPanel(brick);
            initNextBricksPanel(brick);
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
        rectangle.setFill(colourMapper.getFillColor(color));
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

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreValue != null && integerProperty != null) {
            scoreValue.textProperty().bind(integerProperty.asString());
        }
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void displayHighScores(GameMode mode, HighScore manager) {
        gameMessage.displayHighScores(mode, manager);
    }

    public void showSprintComplete(long completionTime, boolean isNewRecord, HighScore manager) {
        timeLine.stop();
        gameMessage.showSprintComplete(completionTime, isNewRecord, manager);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void showBlitzComplete(int finalScore, boolean isNewRecord, HighScore manager) {
        timeLine.stop();
        gameMessage.showBlitzComplete(finalScore, isNewRecord, manager);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void showPitfallGameOver(int finalLevel, int finalScore, boolean isNewRecord, HighScore manager) {
        timeLine.stop();
        gameMessage.showPitfallGameOver(finalLevel, finalScore, isNewRecord, manager);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        gameMessage.clearNotifications();
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