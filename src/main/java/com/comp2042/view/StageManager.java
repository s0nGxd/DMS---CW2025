package com.comp2042.view;

import com.comp2042.controllers.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class StageManager {
    private static StageManager instance;
    private Stage primaryStage;
    private double lastWidth = 600;
    private double lastHeight = 750;

    private boolean isInGame = false;

    public void setInGame(boolean inGame) {
        this.isInGame = inGame;
    }

    private StageManager() {}

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setFullScreenExitHint("");  // ADD THIS - Disables ESC to exit fullscreen
        this.primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);  // ADD THIS
        setupStageListeners();
    }

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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public double getLastWidth() {
        return lastWidth;
    }

    public double getLastHeight() {
        return lastHeight;
    }

    @FunctionalInterface
    public interface SceneInitializer {
        void initialize(Object controller);
    }
}