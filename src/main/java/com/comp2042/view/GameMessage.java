package com.comp2042.view;

import com.comp2042.data.HighScore;
import com.comp2042.events.GameMode;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Manages game completion and notification messages.
 * Handles mode-specific completion screens and high score displays.
 */

public class GameMessage {

    private final Group groupNotification;
    private final Label progressLabel;
    private final UILayoutManager layoutManager;

    /**
     * Constructs a GameMessage manager.
     * @param groupNotification the notification display group
     * @param progressLabel the label showing game progress
     * @param layoutManager the UI layout manager for centering
     */
    public GameMessage(Group groupNotification, Label progressLabel, UILayoutManager layoutManager) {
        this.groupNotification = groupNotification;
        this.progressLabel = progressLabel;
        this.layoutManager = layoutManager;
    }

    /**
     * Displays high scores for the current game mode.
     * @param mode the current game mode
     * @param manager the high score manager
     */
    public void displayHighScores(GameMode mode, HighScore manager) {
        if (progressLabel == null) return;

        String highScoreText = "";
        switch (mode) {
            case SPRINT:
                long bestTime = manager.getSprintBestTime();
                if (bestTime < 999999999) {
                    highScoreText = "Best: " + HighScore.formatTime(bestTime);
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

        final String scoreText = highScoreText;
        Platform.runLater(() -> {
            if (progressLabel != null) {
                String currentText = progressLabel.getText();
                if (currentText == null || currentText.isEmpty() || currentText.equals("Zen Mode")) {
                    progressLabel.setText(scoreText);
                }
            }
        });
        groupNotification.toFront();
        Platform.runLater(layoutManager::centerNotification);
    }

    /**
     * Shows the Sprint mode completion message with time.
     * @param completionTime the time taken to complete in milliseconds
     * @param isNewRecord whether this is a new record
     * @param manager the high score manager
     */
    public void showSprintComplete(long completionTime, boolean isNewRecord, HighScore manager) {
        String timeStr = HighScore.formatTime(completionTime);
        String message = "40 LINES CLEARED!\n" + timeStr;
        if (isNewRecord) {
            message += "\n✨ NEW RECORD! ✨";
        } else {
            long bestTime = manager.getSprintBestTime();
            message += "\nBest: " + HighScore.formatTime(bestTime);
        }
        showCompletionMessage(message, isNewRecord);
    }

    /**
     * Shows the Blitz mode completion message with score.
     * @param finalScore the final score achieved
     * @param isNewRecord whether this is a new record
     * @param manager the high score manager
     */
    public void showBlitzComplete(int finalScore, boolean isNewRecord, HighScore manager) {
        String message = "TIME'S UP!\nScore: " + finalScore;
        if (isNewRecord) {
            message += "\n✨ NEW RECORD! ✨";
        } else {
            int highScore = manager.getBlitzHighScore();
            message += "\nHigh Score: " + highScore;
        }
        showCompletionMessage(message, isNewRecord);
    }

    /**
     * Shows the Pitfall mode game over message.
     * @param finalLevel the final level reached
     * @param finalScore the final score achieved
     * @param isNewRecord whether this is a new record
     * @param manager the high score manager
     */
    public void showPitfallGameOver(int finalLevel, int finalScore, boolean isNewRecord, HighScore manager) {
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

    /**
     * Display the completion message when the game ends
     */
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

        groupNotification.toFront();
        Platform.runLater(layoutManager::centerNotification);
    }

    /**
     * Clears all notifications from the display.
     */
    public void clearNotifications() {
        groupNotification.getChildren().clear();
    }
}