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
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    private StageManager stageManager;

    private GameMode currentGameMode = GameMode.ZEN; // Default mode
    private Stage mainStage;
    private MainMenuController mainMenuController;

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

    @FXML
    private GridPane holdBrickPanel;
    @FXML
    private VBox nextBricksPanel;
    @FXML
    private VBox heldBrickBox;
    @FXML
    private VBox nextBricksBox;

    @FXML
    private VBox footerArea;

    @FXML
    private Label scoreValue;

    @FXML
    private HBox headerArea;

    @FXML
    private Label gameModeLabel;

    @FXML
    private Label progressLabel; // For SPRINT/PITFALL progress

    @FXML
    private Label highScoreLabel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private int[][] currentBoardMatrix;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private Stage stage;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }

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
    
    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
        // Used to configure game modes
    }

    public GameMode getCurrentGameMode() {
        return currentGameMode;
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
        if (stage == null) return;

        // Get window dimensions
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
            double headerY = startY;
            headerArea.setLayoutX(headerX);
            headerArea.setLayoutY(headerY);
        }

        // Position game board below header
        double gameBoardY = startY + headerHeight + verticalSpacing;
        double gameBoardX = startX + heldBrickWidth + sideSpacing;
        gameBoard.setLayoutX(gameBoardX);
        gameBoard.setLayoutY(gameBoardY);

        // Position held brick panel to the left of game panel
        if (heldBrickBox != null) {
            double holdBoxX = startX;
            double holdBoxY = gameBoardY + (gamePanelHeight - heldBrickBox.getHeight()) / 2;
            heldBrickBox.setLayoutX(holdBoxX);
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

        // Update brick position
        if (eventListener != null) {
            ViewData currentViewData = eventListener.getCurrentViewData();
            if (currentViewData != null) {
                refreshBrick(currentViewData);
            }
        }

        centerNoti();
    }

    private void toggleFullScreen() {
        if (stageManager != null) {
            stageManager.toggleFullscreen();
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
                // KEYS THAT NEEDS TO WORK OUTSIDE GAMEPLAY
                //ESCAPE KEY
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    returnToMainMenu();
                    keyEvent.consume();
                    return;
                }

                // F11 FOR FULLSCREEN
                if (keyEvent.getCode() == KeyCode.F11) {
                    toggleFullScreen();
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

                    // ESCAPE KEY BRINGS TO MAIN MENU
                    if (keyEvent.getCode() == KeyCode.ESCAPE) {
                        returnToMainMenu();
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

        // Initialize the hold brick and next brick panel
        initHoldBrickPanel(brick);
        initNextBricksPanel(brick);

        // Get fall speed
        int speed = 400;  // Default speed
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
                long elapsed = System.currentTimeMillis() -
                        simpleBoard.getGameStartTime();
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

    public void gameWon() {
        timeLine.stop();

        // Create a win message similar to game over
        Label winLabel = new Label("YOU WIN!");
        winLabel.getStyleClass().add("gameOverStyle");

        BorderPane winPanel = new BorderPane();
        winPanel.setCenter(winLabel);
        winPanel.setVisible(true);

        // Add to notification group
        groupNotification.getChildren().clear();
        groupNotification.getChildren().add(winPanel);

        isGameOver.setValue(Boolean.TRUE);
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
                        rectangle.setFill(getFillColor(heldBrickData[i][j]));
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

                // Calculate the size needed for this brick
                int rows = brickData.length;
                int cols = brickData[0].length;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (brickData[i][j] != 0) {
                            Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                            rectangle.setFill(getFillColor(brickData[i][j]));
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
        if (scoreValue != null && integerProperty != null) {
            scoreValue.textProperty().bind(integerProperty.asString());
        }
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void displayHighScores(GameMode mode, HighScoreManager manager) {
        if (progressLabel == null) return;

        String highScoreText = "";
        switch (mode) {
            case SPRINT:
                long bestTime = manager.getSprintBestTime();
                if (bestTime < 999999999) {
                    highScoreText = "Best: " + HighScoreManager.formatTime(bestTime);
                } else {
                    highScoreText = "Best: --:--";
                }
                break;
            case BLITZ:
                int blitzHigh = manager.getBlitzHighScore();
                highScoreText = "High: " + blitzHigh;
                break;
            case PITFALL:
                int pitfallLevel = manager.getPitfallHighLevel();
                int pitfallScore = manager.getPitfallHighScore();
                highScoreText = "Best: Lv." + pitfallLevel + " / " + pitfallScore;
                break;
            case ZEN:
                highScoreText = "Zen Mode";
                break;
        }

        // Display on the progress label initially
        final String scoreText = highScoreText;
        javafx.application.Platform.runLater(() -> {
            if (progressLabel != null) {
                String currentText = progressLabel.getText();
                if (currentText == null || currentText.isEmpty() || currentText.equals("Zen Mode")) {
                    progressLabel.setText(scoreText);
                }
            }
        });
    }

    public void showSprintComplete(long completionTime, boolean isNewRecord, HighScoreManager manager) {
        timeLine.stop();

        String timeStr = HighScoreManager.formatTime(completionTime);
        String message = "40 LINES CLEARED!\n" + timeStr;
        if (isNewRecord) {
            message += "\n✨ NEW RECORD! ✨";
        } else {
            long bestTime = manager.getSprintBestTime();
            message += "\nBest: " + HighScoreManager.formatTime(bestTime);
        }

        showCompletionMessage(message, isNewRecord);
    }

    public void showBlitzComplete(int finalScore, boolean isNewRecord, HighScoreManager manager) {
        timeLine.stop();

        String message = "TIME'S UP!\nScore: " + finalScore;
        if (isNewRecord) {
            message += "\n✨ NEW RECORD! ✨";
        } else {
            int highScore = manager.getBlitzHighScore();
            message += "\nHigh Score: " + highScore;
        }

        showCompletionMessage(message, isNewRecord);
    }

    public void showPitfallGameOver(int finalLevel, int finalScore, boolean isNewRecord, HighScoreManager manager) {
        timeLine.stop();

        String message = "GAME OVER\nLevel " + finalLevel + "\nScore: " + finalScore;
        if (isNewRecord) {
            message += "\n✨ NEW RECORD! ✨";
        } else {
            int highLevel = manager.getPitfallHighLevel();
            int highScore = manager.getPitfallHighScore();
            message += "\nBest: Lv." + highLevel + " / " + highScore;
        }

        showCompletionMessage(message, isNewRecord);
    }

    private void showCompletionMessage(String message, boolean isNewRecord) {
        Label completeLabel = new Label(message);
        completeLabel.getStyleClass().add(isNewRecord ? "newRecordStyle" : "gameCompleteStyle");
        completeLabel.setStyle("-fx-text-alignment: center; -fx-wrap-text: true;");
        completeLabel.setMaxWidth(400);

        BorderPane completePanel = new BorderPane();
        completePanel.setCenter(completeLabel);
        completePanel.setVisible(true);
        completePanel.setMaxWidth(450);
        completePanel.setMaxHeight(300);

        groupNotification.getChildren().clear();
        groupNotification.getChildren().add(completePanel);

        // Center the notification
        Platform.runLater(() -> centerNoti());

        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        groupNotification.getChildren().clear();
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
