package com.comp2042.view;

import com.comp2042.testutil.JavaFXBaseTest;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UILayoutManagerTest extends JavaFXBaseTest {

    @Test
    @DisplayName("Layout Manager instantiates without error")
    void testInstantiation() {
        // We verify that we can create the manager with real Nodes (thanks to JavaFXBaseTest)
        // Testing exact pixel positioning is fragile in unit tests, simpler to ensure no crashes.

        // Mock Stage is hard, pass null if handled, or create a dummy stage on FX thread
        // UILayoutManager checks for stage == null in updateLayout

        UILayoutManager manager = new UILayoutManager(
                null,
                new BorderPane(),
                new GridPane(),
                new GridPane(),
                new HBox(),
                new VBox(),
                new VBox(),
                new VBox(),
                new Group()
        );

        assertDoesNotThrow(manager::updateLayout);
    }
}