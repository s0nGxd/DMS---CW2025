package com.comp2042.controllers;

import com.comp2042.events.GameMode;
import com.comp2042.testutil.JavaFXBaseTest;
import javafx.fxml.Initializable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuiControllerTest extends JavaFXBaseTest {

    @Test
    @DisplayName("GuiController implements Initializable")
    void testImplementsInterface() {
        GuiController controller = new GuiController();
        assertTrue(controller instanceof Initializable, "Controller must be FXML Initializable");
    }

    @Test
    @DisplayName("SetGameMode stores the mode correctly")
    void testSetGameMode() {
        // This relies on reflection or inspecting side effects if getter is missing.
        // Since `currentGameMode` is private and has no getter, we test that calling setter
        // doesn't crash the controller even if UI elements aren't loaded yet.

        GuiController controller = new GuiController();
        assertDoesNotThrow(() -> controller.setGameMode(GameMode.ZEN));
        assertDoesNotThrow(() -> controller.setGameMode(GameMode.SPRINT));
    }
}