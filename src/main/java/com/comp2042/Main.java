package com.comp2042;

import com.comp2042.controllers.MainMenuController;
import com.comp2042.view.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the TetrisJFX application.
 *
 * <p>Initializes the JavaFX application, sets up the StageManager,
 * and displays the main menu on startup. This class extends JavaFX Application
 * and handles the initial scene configuration.</p>
 */

public class Main extends Application {

    /**
     * Starts the JavaFX application and displays the main menu.
     *
     * <p>Initializes the StageManager with the primary stage, sets the window title,
     * and loads the main menu FXML scene. The stage is then displayed to the user.</p>
     *
     * @param primaryStage the primary stage provided by JavaFX framework
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize StageManager
        StageManager stageManager = StageManager.getInstance();
        stageManager.setPrimaryStage(primaryStage);

        primaryStage.setTitle("TetrisJFX");

        // Use StageManager for initial scene
        stageManager.switchScene("mainMenu.fxml", controller -> {
            MainMenuController menuController = (MainMenuController) controller;
            menuController.setStage(primaryStage);
        });

        primaryStage.show();
    }
}
