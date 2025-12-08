package com.comp2042.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DownDataTest {

    @Test
    @DisplayName("DownData encapsulates ClearRow and ViewData")
    void testDownDataEncapsulation() {
        ClearRow clearRow = new ClearRow(1, new int[0][0], 100);
        ViewData viewData = new ViewData(new int[0][0], 0, 0, new int[0][0], 0, null, null);

        DownData downData = new DownData(clearRow, viewData);

        assertSame(clearRow, downData.getClearRow());
        assertSame(viewData, downData.getViewData());
    }
}