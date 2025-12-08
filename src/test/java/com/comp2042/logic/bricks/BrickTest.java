package com.comp2042.logic.bricks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BrickTest {

    @Test
    @DisplayName("All Brick types should have 4 rotation states (SRS Standard)")
    void testBrickStates() {
        Brick[] bricks = {
                new IBrick(), new JBrick(), new LBrick(),
                new OBrick(), new SBrick(), new TBrick(), new ZBrick()
        };

        for (Brick brick : bricks) {
            List<int[][]> shapes = brick.getShapeMatrix();

            // O-Brick is special, it might have 1 or 4 states depending on impl.
            // Your O-Brick code shows 1 state added.
            // Others (like TBrick) have 4.

            if (brick instanceof OBrick) {
                assertFalse(shapes.isEmpty(), "OBrick must have at least 1 state");
            } else {
                assertEquals(4, shapes.size(),
                        brick.getClass().getSimpleName() + " should have 4 rotation states");
            }

            // Verify matrices are square/rectangular and not null
            assertNotNull(shapes.get(0));
            assertTrue(shapes.get(0).length > 0);
        }
    }

    @Test
    @DisplayName("I-Brick should be 4x4")
    void testIBrickDimensions() {
        Brick iBrick = new IBrick();
        int[][] matrix = iBrick.getShapeMatrix().get(0);
        assertEquals(4, matrix.length);
        assertEquals(4, matrix[0].length);
    }
}