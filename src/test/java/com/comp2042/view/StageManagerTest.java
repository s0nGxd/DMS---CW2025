package com.comp2042.view;

import com.comp2042.testutil.JavaFXBaseTest;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class StageManagerTest extends JavaFXBaseTest {

    @Test
    @DisplayName("StageManager adheres to Singleton pattern")
    void testSingleton() {
        StageManager instance1 = StageManager.getInstance();
        StageManager instance2 = StageManager.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "Both instances should point to the same object");
    }

    @Test
    @DisplayName("Fullscreen toggle logic updates stage state")
    void testToggleFullscreen() throws InterruptedException {
        // Stage interactions must run on the JavaFX Thread
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            StageManager manager = StageManager.getInstance();
            manager.setPrimaryStage(stage);

            // Initial state
            stage.setFullScreen(false);

            // Toggle On
            manager.toggleFullscreen();
            assertTrue(stage.isFullScreen(), "Stage should be fullscreen after toggle");

            // Toggle Off
            manager.toggleFullscreen();
            assertFalse(stage.isFullScreen(), "Stage should not be fullscreen after second toggle");

            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Test timed out waiting for FX thread");
    }
}