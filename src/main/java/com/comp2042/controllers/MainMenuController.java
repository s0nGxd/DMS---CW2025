package com.comp2042.controllers;

import com.comp2042.events.GameMode;
import com.comp2042.view.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main menu scene.
 * Handles game mode selection and navigation to gameplay.
 */

public class MainMenuController implements Initializable {

    @FXML
    private ScrollPane gameModeScrollPane;

    @FXML
    private VBox gameModesContainer;

    private Stage stage;

    /**
     * Initializes the menu UI components.
     * @param location the FXML location
     * @param resources the resource bundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameModeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gameModeScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    /**
     * Sets the stage for this controller.
     * @param stage the primary stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles Zen mode selection.
     * @param event the mouse click event
     */
    @FXML
    private void selectZenMode(MouseEvent event) {
        startGame(GameMode.ZEN);
    }

    /**
     * Handles Sprint mode selection.
     * @param event the mouse click event
     */
    @FXML
    private void selectSprintMode(MouseEvent event) {
        startGame(GameMode.SPRINT);
    }

    /**
     * Handles Blitz mode selection.
     * @param event the mouse click event
     */
    @FXML
    private void selectBlitzMode(MouseEvent event) {
        startGame(GameMode.BLITZ);
    }

    /**
     * Handles Pitfall mode selection.
     * @param event the mouse click event
     */

    @FXML
    private void selectPitfallMode(MouseEvent event) {
        startGame(GameMode.PITFALL);
    }

    /**
     * Starts the game with the selected mode.
     * @param mode the selected game mode
     */
    private void startGame(GameMode mode) {
        // Use StageManager to preserve window dimensions
        StageManager stageManager = StageManager.getInstance();
        stageManager.switchScene("gameLayout.fxml", controller -> {
            GuiController guiController = (GuiController) controller;
            guiController.setStage(stageManager.getPrimaryStage());
            guiController.setGameMode(mode);
            guiController.setMainMenuStage(stageManager.getPrimaryStage(), this);

            // Initialize game after controller is set up
            new GameController(guiController, mode);
        });
    }
}