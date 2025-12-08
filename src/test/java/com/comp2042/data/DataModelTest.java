package com.comp2042.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataModelTest {

    @Test
    @DisplayName("NextShapeInfo holds correct data and position")
    void testNextShapeInfo() {
        int[][] shape = {{1, 1}};
        NextShapeInfo info = new NextShapeInfo(shape, 2);

        assertEquals(2, info.getPosition());
        // Verify Deep Copy if implemented, or at least content equality
        assertArrayEquals(shape[0], info.getShape()[0]);
    }

    @Test
    @DisplayName("ClearRow holds lines removed and bonus")
    void testClearRow() {
        int[][] matrix = new int[2][2];
        ClearRow clearRow = new ClearRow(3, matrix, 300);

        assertEquals(3, clearRow.getLinesRemoved());
        assertEquals(300, clearRow.getScoreBonus());
        assertNotNull(clearRow.getNewMatrix());
    }

    @Test
    @DisplayName("DownData holds View and ClearRow")
    void testDownData() {
        ClearRow cr = new ClearRow(1, new int[0][0], 100);
        ViewData vd = new ViewData(new int[0][0], 0, 0, new int[0][0], 0, null, null);

        DownData dd = new DownData(cr, vd);

        assertSame(cr, dd.getClearRow());
        assertSame(vd, dd.getViewData());
    }
}