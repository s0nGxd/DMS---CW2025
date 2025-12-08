package com.comp2042.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NextShapeInfoTest {

    @Test
    @DisplayName("Stores shape matrix and position correctly")
    void testNextShapeInfoStorage() {
        int[][] matrix = {{1, 0}, {0, 1}};
        int position = 2;

        NextShapeInfo info = new NextShapeInfo(matrix, position);

        assertEquals(position, info.getPosition());
        assertArrayEquals(matrix, info.getShape());

        // Ensure deep copy (modification of source shouldn't affect stored data)
        matrix[0][0] = 9;
        assertEquals(1, info.getShape()[0][0]);
    }
}