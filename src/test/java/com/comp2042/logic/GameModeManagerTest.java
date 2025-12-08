package com.comp2042.logic;

import com.comp2042.events.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModeManagerTest {

    private GameModeManager manager;

    @BeforeEach
    void setUp() {
        manager = new GameModeManager();
    }

    @Test
    @DisplayName("Zen Mode should not have win conditions")
    void testZenMode() {
        manager.setGameMode(GameMode.ZEN);
        manager.addLines(100); // Simulate clearing many lines

        assertFalse(manager.isSprintComplete(), "Zen mode should not trigger Sprint completion");
        assertFalse(manager.isBlitzTimeUp(), "Zen mode should not trigger Blitz timeout");
    }

    @Test
    @DisplayName("Sprint Mode should finish at 40 lines")
    void testSprintMode() {
        manager.setGameMode(GameMode.SPRINT);

        manager.addLines(39);
        assertFalse(manager.isSprintComplete(), "Should not be complete at 39 lines");

        manager.addLines(1);
        assertTrue(manager.isSprintComplete(), "Should be complete at 40 lines");
    }

    @Test
    @DisplayName("Pitfall Mode should increase level and speed")
    void testPitfallMode() {
        manager.setGameMode(GameMode.PITFALL);
        int initialSpeed = manager.getFallSpeed();
        int initialLevel = manager.getCurrentLevel();

        // Simulate clearing 10 lines to level up
        manager.addLines(10); // Logic: lines / 10 + 1

        assertTrue(manager.getCurrentLevel() > initialLevel, "Level should increase");
        assertTrue(manager.getFallSpeed() < initialSpeed, "Fall speed (delay) should decrease (get faster)");
    }

    @Test
    @DisplayName("Manager resets correctly")
    void testReset() {
        manager.addLines(10);
        manager.reset();
        assertEquals(0, manager.getLinesCleared());
        assertEquals(1, manager.getCurrentLevel());
    }
}