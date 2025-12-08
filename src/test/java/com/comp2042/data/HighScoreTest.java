package com.comp2042.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreTest {

    // Note: We avoid testing load/save methods to prevent creating files on the test runner.
    // We focus on the logic methods.

    @Test
    @DisplayName("Format Time converts milliseconds to HH:MM:SS.mmm")
    void testFormatTime() {
        // 1 hour, 1 minute, 1 second, 500 ms
        long millis = 3600000 + 60000 + 1000 + 500;
        String result = HighScore.formatTime(millis);

        assertEquals("01:01:01.500", result);
    }

    @Test
    @DisplayName("Format Time handles zero correctly")
    void testFormatTimeZero() {
        assertEquals("00:00:00.000", HighScore.formatTime(0));
    }

    // Since HighScore is a Singleton that loads from file on init,
    // testing state requires careful management or mocking.
    // For unit tests, testing the static helper 'formatTime' is the most "meaningful"
    // logic test that doesn't depend on external files.
}