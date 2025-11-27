package com.comp2042;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private ScrollPane gameModeScrollPane;

    @FXML
    private VBox gameModesContainer;

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameModeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gameModeScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void selectZenMode(MouseEvent event) {
        startGame(GameMode.ZEN);
    }

    @FXML
    private void selectSprintMode(MouseEvent event) {
        startGame(GameMode.SPRINT);
    }

    @FXML
    private void selectBlitzMode(MouseEvent event) {
        startGame(GameMode.BLITZ);
    }

    @FXML
    private void selectPitfallMode(MouseEvent event) {
        startGame(GameMode.PITFALL);
    }

    private void startGame(GameMode mode) {
        StageManager stageManager = StageManager.getInstance();
        stageManager.switchScene("gameLayout.fxml", controller -> {
            GuiController guiController = (GuiController) controller;
            guiController.setStage(stageManager.getPrimaryStage());
            guiController.setGameMode(mode);
            guiController.setStageManager(stageManager);
            new GameController(guiController);
        });
    }

    public void showMainMenu() {
        StageManager stageManager = StageManager.getInstance();
        stageManager.switchScene("mainMenu.fxml", controller -> {
            MainMenuController menuController = (MainMenuController) controller;
            menuController.setStage(stageManager.getPrimaryStage());
        });
    }
}