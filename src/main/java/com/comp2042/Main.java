package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

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


    public static void main(String[] args) {
        launch(args);
    }
}
