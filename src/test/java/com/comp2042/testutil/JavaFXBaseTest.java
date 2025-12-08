package com.comp2042.testutil;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

public class JavaFXBaseTest {

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized, ignore
        }
    }
}