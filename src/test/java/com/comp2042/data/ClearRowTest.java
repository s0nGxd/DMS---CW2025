package com.comp2042.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClearRowTest {

    @Test
    @DisplayName("ClearRow stores line clear details")
    void testClearRowDetails() {
        int linesRemoved = 4;
        int scoreBonus = 1200;
        int[][] newMatrix = {{0}, {0}};

        ClearRow clearRow = new ClearRow(linesRemoved, newMatrix, scoreBonus);

        assertEquals(linesRemoved, clearRow.getLinesRemoved());
        assertEquals(scoreBonus, clearRow.getScoreBonus());
        assertArrayEquals(newMatrix, clearRow.getNewMatrix());
    }
}