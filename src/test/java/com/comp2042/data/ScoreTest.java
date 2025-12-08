package com.comp2042.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    @Test
    @DisplayName("Score calculation and property binding")
    void testScore() {
        Score score = new Score();

        assertEquals(0, score.scoreProperty().get());

        score.add(100);
        assertEquals(100, score.scoreProperty().get());

        score.add(50);
        assertEquals(150, score.scoreProperty().get());

        score.reset();
        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    @DisplayName("Time formatting utility")
    void testTimeFormatting() {
        // 65000ms = 1 minute 5 seconds
        long millis = 65000;
        String formatted = HighScore.formatTime(millis);

        // Expected format: HH:MM:SS.mmm
        assertEquals("00:01:05.000", formatted);
    }
}