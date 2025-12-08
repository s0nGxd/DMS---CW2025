package com.comp2042.view;

import com.comp2042.controllers.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Singleton manager for JavaFX stage and scene transitions.
 * Handles window state preservation and fullscreen toggling.
 */

public class StageManager {
    private static StageManager instance;
    private Stage primaryStage;
    private double lastWidth = 600;
    private double lastHeight = 750;

    private boolean isInGame = false;

    /**
     * Sets whether the game is currently in gameplay state.
     * @param inGame true if in gameplay, false if in menu
     */
    public void setInGame(boolean inGame) {
        this.isInGame = inGame;
    }

    private StageManager() {}

    /**
     * Gets the singleton instance of StageManager.
     * @return the shared StageManager instance
     */
    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    /**
     * Sets the primary stage for the application.
     * @param stage the primary JavaFX stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setFullScreenExitHint("");  // ADD THIS - Disables ESC to exit fullscreen
        this.primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);  // ADD THIS
        setupStageListeners();
    }

    /**
     * Sets up listeners for stage events and keyboard shortcuts.
     */
    private void setupStageListeners() {
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.F11) {
                        toggleFullscreen();
                        event.consume();
                    }
                    if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        if (isInGame) {
                            // During game: go to menu, stay fullscreen
                            setInGame(false);
                            switchScene("mainMenu.fxml", controller -> {
                                MainMenuController menuController = (MainMenuController) controller;
                                menuController.setStage(primaryStage);
                            });
                            event.consume();

                        } else if (primaryStage.isFullScreen()){
                            // In menu: exit fullscreen manually
                            primaryStage.setFullScreen(false);
                            primaryStage.setWidth(lastWidth);
                            primaryStage.setHeight(lastHeight);
                            event.consume();
                        }
                    }
                });
            }
        });
    }

    /**
     * Switches to a new FXML scene while preserving window state.
     * @param fxmlPath the path to the FXML file
     * @param initializer callback to initialize the controller
     */
    public void switchScene(String fxmlPath, SceneInitializer initializer) {
        try {
            URL location = getClass().getClassLoader().getResource(fxmlPath);
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();

            // Preserve window state
            boolean isMaximized = primaryStage.isMaximized();
            boolean isFullscreen = primaryStage.isFullScreen();

            // Use scene dimensions, not window dimensions
            Scene currentScene = primaryStage.getScene();
            double sceneWidth = (currentScene != null) ? currentScene.getWidth() : lastWidth;
            double sceneHeight = (currentScene != null) ? currentScene.getHeight() : lastHeight;

            Scene scene = new Scene(root, sceneWidth, sceneHeight);
            primaryStage.setScene(scene);

            // Restore window state after scene is set
            if (isMaximized) {
                primaryStage.setMaximized(true);
            }
            if (isFullscreen) {
                primaryStage.setFullScreen(true);
            }

            // Initialize controller with stage reference
            if (initializer != null) {
                initializer.initialize(fxmlLoader.getController());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggles fullscreen mode on/off.
     */
    public void toggleFullscreen() {
        if (primaryStage != null) {
            if (primaryStage.isFullScreen()) {
                // Exiting fullscreen - restore dimensions
                primaryStage.setFullScreen(false);
                primaryStage.setWidth(lastWidth);
                primaryStage.setHeight(lastHeight);
            } else {
                // Entering fullscreen
                lastWidth = primaryStage.getWidth();
                lastHeight = primaryStage.getHeight();
                primaryStage.setFullScreen(true);
            }
        }
    }

    /**
     * Gets the primary stage.
     * @return the primary JavaFX stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @FunctionalInterface
    public interface SceneInitializer {
        void initialize(Object controller);
    }
}