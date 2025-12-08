package com.comp2042.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ViewDataTest {

    @Test
    @DisplayName("ViewData correctly stores and retrieves data")
    void testViewDataStorage() {
        int[][] brickData = {{1}};
        int[][] nextBrick = {{2}};
        int[][] heldBrick = {{3}};

        ViewData data = new ViewData(brickData, 5, 10, nextBrick, 15, heldBrick, null);

        assertEquals(5, data.getxPosition());
        assertEquals(10, data.getyPosition());
        assertEquals(15, data.getGhostPosition());

        // Test Copying/Immutability (if implemented in getter)
        int[][] retrievedBrick = data.getBrickData();
        assertEquals(1, retrievedBrick[0][0]);
    }
}