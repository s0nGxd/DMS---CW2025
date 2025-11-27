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
        try {
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            GuiController controller = fxmlLoader.getController();

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            controller.setStage(stage);
            controller.setGameMode(mode);

            // Used to return to main menu
            controller.setMainMenuStage(stage, this);

            new GameController(controller);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMainMenu() {
        try {
            URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            MainMenuController controller = fxmlLoader.getController();

            Scene scene = new Scene(root, 600, 700);
            stage.setScene(scene);
            controller.setStage(stage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}