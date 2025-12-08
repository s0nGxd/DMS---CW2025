package com.comp2042.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * JavaFX panel displaying the "GAME OVER" message.
 * Shown when the player loses the game.
 */

public class GameOverPanel extends BorderPane {

    /**
     * Constructs a GameOverPanel with styled game over text.
     */
    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }

}
