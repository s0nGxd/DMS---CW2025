package com.comp2042.view;

import com.comp2042.testutil.JavaFXBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationPanelTest extends JavaFXBaseTest {

    @Test
    @DisplayName("Notification Panel creates label with text")
    void testCreation() {
        NotificationPanel panel = new NotificationPanel("Bonus +100");
        assertNotNull(panel.getCenter());
        // Verify it is a JavaFX component
        assertTrue(panel.getCenter() instanceof javafx.scene.control.Label);
    }
}