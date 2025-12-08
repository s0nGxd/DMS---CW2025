package com.comp2042.controllers;

import com.comp2042.constant.GameConstants;
import com.comp2042.render.*;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX controller managing the game UI and user interactions.
 * Coordinates rendering, input handling, and game loop timing.
 */
public class GuiController implements Initializable {

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

    private InputEventListener eventListener;
    private Timeline timeLine;
    private Stage stage;
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private UILayoutManager layoutManager;
    private boolean isExiting = false;
    private GameMessage gameMessage;
    private InputHandler inputHandler;
    private BoardRenderer boardRenderer;
    private BrickPanelRenderer brickPanelRenderer;
    private NextPanelRenderer nextBrickRenderer;
    private GhostBrickRenderer ghostRenderer;
    private ColourMapper colourMapper;

    /**
     * Sets the stage for this controller.
     * @param stage the primary JavaFX stage
     */
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

    /**
     * Sets the game mode for this session.
     * @param mode the game mode to play
     */
    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
    }

    /**
     * Sets the main menu stage and controller.
     * @param stage the main menu stage
     * @param menuController the main menu controller
     */
    public void setMainMenuStage(Stage stage, MainMenuController menuController) {
        this.mainStage = stage;
        this.mainMenuController = menuController;
    }

    /**
     * Returns to the main menu scene.
     */
    private void returnToMainMenu() {
        // Prevent any further game actions
        isExiting = true;

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

    /**
     * Initializes the controller and sets up UI components.
     * Loads fonts, initializes renderers, and configures keyboard input handling.
     * @param location the location used to resolve relative paths for the root object
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (rootPane != null) {
            rootPane.setOpacity(0);
        }
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        colourMapper = new ColourMapper();
        boardRenderer = new BoardRenderer(gamePanel, colourMapper);
        brickPanelRenderer = new BrickPanelRenderer(gamePanel, colourMapper);
        nextBrickRenderer = new NextPanelRenderer(holdBrickPanel, nextBricksPanel, colourMapper);
        inputHandler = new InputHandler(isPause, isGameOver);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(keyEvent -> {
            inputHandler.handleKeyPress(
                    keyEvent.getCode(),
                    this::returnToMainMenu,
                    () -> newGame(null),
                    this::refreshBrick,
                    this::moveDown,
                    this::dropDown
            );
            keyEvent.consume();
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    /**
     * Initializes the game view with the initial board and brick state.
     * Sets up the game loop timeline and begins the game.
     * @param boardMatrix the initial state of the game board
     * @param brick the initial brick view data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        brickPanelRenderer.renderBrick(brick);
        nextBrickRenderer.renderHoldBrick(brick.getHeldBrickData());
        nextBrickRenderer.renderNextBricks(brick.getNextBricksData());

        ghostRenderer = new GhostBrickRenderer(boardRenderer.initializeBoard(boardMatrix), colourMapper);

        // Position brick panel FIRST, before adding any rectangles
        Platform.runLater(() -> {
            if (layoutManager != null) {
                layoutManager.updateBrickPosition(brick);
                layoutManager.updateLayout();
                refreshBrick(brick);

                if (rootPane != null) {
                    rootPane.setOpacity(1);
                }
            }
        });

        // Update game mode UI BEFORE starting timeline to prevent flash
        updateGameModeUI();

        // Get fall speed
        int speed = GameConstants.DEFAULT_FALL_SPEED;
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

    /**
     * Updates the game mode UI elements (timer, lines, level).
     */
    private void updateGameModeUI() {
        if (!(eventListener instanceof GameController)) return;

        GameController gameController = (GameController) eventListener;
        SimpleBoard simpleBoard = gameController.getSimpleBoard();

        if (simpleBoard == null || progressLabel == null) return;

        switch (simpleBoard.getGameMode()) {
            case SPRINT:
                int remaining = GameConstants.SPRINT_TARGET_LINES - simpleBoard.getLinesCleared();
                progressLabel.setText("Lines: " + remaining);
                break;
            case BLITZ:
                long elapsed = System.currentTimeMillis() - simpleBoard.getGameStartTime();
                // Prevent negative time display on first frame
                if (elapsed < 0) elapsed = 0;
                int seconds = (int) ((GameConstants.BLITZ_TIME_LIMIT - elapsed) / 1000);
                progressLabel.setText("Time: " + seconds + "s");
                break;
            case PITFALL:
                progressLabel.setText("Level: " + simpleBoard.getCurrentLevel());
                // Update speed
                if (timeLine != null) {
                    timeLine.setRate(GameConstants.DEFAULT_FALL_SPEED / (double) simpleBoard.getFallSpeed());
                }
                break;
            case ZEN:
                progressLabel.setText("Zen Mode");
                break;
        }
    }

    /**
     * Refreshes the visual representation of the current brick.
     * Updates the board background, ghost brick, active brick, and side panels.
     * @param brick the view data containing current brick information
     */
    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Refresh board background
            if (eventListener instanceof GameController) {
                boardRenderer.refreshBoard(((GameController) eventListener).getSimpleBoard().getBoardMatrix());
            }

            // Render new Ghost Brick
            ghostRenderer.renderGhostBrick(brick);

            brickPanelRenderer.renderBrick(brick);

            // Update Hold & Next Bricks Panel
            nextBrickRenderer.renderHoldBrick(brick.getHeldBrickData());
            nextBrickRenderer.renderNextBricks(brick.getNextBricksData());
        }
    }

    /**
     * Refreshes the game board background display.
     * @param board the current state of the game board matrix
     */
    public void refreshGameBackground(int[][] board) {
        boardRenderer.refreshBoard(board);
    }

    /**
     * Handles downward movement of the active brick.
     * Processes the movement event and updates the display with any cleared rows.
     * @param event the move event containing movement information
     */
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

    /**
     * Handles hard drop action, instantly dropping the brick to the bottom.
     * Processes the drop event and updates the display with any cleared rows.
     * @param event the move event containing drop information
     */
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

    /**
     * Sets the input event listener for game events.
     * @param eventListener the listener to handle game input events
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        inputHandler.setEventListener(eventListener);
    }

    /**
     * Binds the score property to the UI label.
     * @param integerProperty the score property to bind
     */
    public void bindScore(IntegerProperty integerProperty) {
        if (scoreValue != null && integerProperty != null) {
            scoreValue.textProperty().bind(integerProperty.asString());
        }
    }

    /**
     * Displays the game over screen.
     */
    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        Platform.runLater(() -> layoutManager.centerNotification());
    }

    /**
     * Displays high scores for the current game mode in the progress label.
     * @param mode the current game mode
     * @param manager the high score manager containing saved records
     */
    public void displayHighScores(GameMode mode, HighScore manager) {
        gameMessage.displayHighScores(mode, manager);
    }

    /**
     * Shows the Sprint mode completion screen with time and record status.
     * @param completionTime the time taken to complete 40 lines in milliseconds
     * @param isNewRecord whether this time is a new record
     * @param manager the high score manager for displaying best times
     */
    public void showSprintComplete(long completionTime, boolean isNewRecord, HighScore manager) {
        timeLine.stop();
        gameMessage.showSprintComplete(completionTime, isNewRecord, manager);
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Shows the Blitz mode completion screen with final score and record status.
     * @param finalScore the final score achieved in 3 minutes
     * @param isNewRecord whether this score is a new record
     * @param manager the high score manager for displaying high scores
     */
    public void showBlitzComplete(int finalScore, boolean isNewRecord, HighScore manager) {
        timeLine.stop();
        gameMessage.showBlitzComplete(finalScore, isNewRecord, manager);
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Shows the Pitfall mode game over screen with level, score, and record status.
     * @param finalLevel the final level reached before game over
     * @param finalScore the final score achieved
     * @param isNewRecord whether level or score is a new record
     * @param manager the high score manager for displaying records
     */
    public void showPitfallGameOver(int finalLevel, int finalScore, boolean isNewRecord, HighScore manager) {
        timeLine.stop();
        gameMessage.showPitfallGameOver(finalLevel, finalScore, isNewRecord, manager);
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Starts a new game session.
     * @param actionEvent the triggering action event
     */
    public void newGame(ActionEvent actionEvent) {
        if (isExiting) return;

        gameMessage.clearNotifications();
        if (!groupNotification.getChildren().contains(gameOverPanel)) {
            groupNotification.getChildren().add(gameOverPanel);
        }
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

}