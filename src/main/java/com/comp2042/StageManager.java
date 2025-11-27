package com.comp2042;

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
    private boolean wasMaximized = false;
    private boolean wasFullscreen = false;

    private StageManager() {}

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        setupStageListeners();
    }

    private void setupStageListeners() {
        // Track scene dimensions instead of window dimensions
        primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldVal, newVal) -> {
                    if (!primaryStage.isMaximized() && !primaryStage.isFullScreen()) {
                        lastWidth = newVal.doubleValue();
                    }
                });

                newScene.heightProperty().addListener((o, oldVal, newVal) -> {
                    if (!primaryStage.isMaximized() && !primaryStage.isFullScreen()) {
                        lastHeight = newVal.doubleValue();
                    }
                });
            }
        });

        primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            wasMaximized = newVal;
        });

        primaryStage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
            wasFullscreen = newVal;
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
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
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