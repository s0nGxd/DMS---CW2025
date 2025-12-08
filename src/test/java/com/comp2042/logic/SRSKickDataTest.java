package com.comp2042.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

class SRSKickDataTest {

    // Helper to represent brick types based on your code
    private static final int I_BRICK = 1;
    private static final int J_BRICK = 2; // Represents J, L, S, T, Z

    @Test
    @DisplayName("I-Brick Kicks: State 0 to 1 (Clockwise)")
    void testIBrickKick0to1() {
        // SRS Rule for I piece 0->1
        // (0,0), (-2,0), (+1,0), (-2,-1), (+1,+2)
        Point[] kicks = SRSKickData.getKickOffsets(I_BRICK, 0, 1);

        assertEquals(5, kicks.length, "Should provide 5 tests");
        assertEquals(new Point(0, 0), kicks[0]);
        assertEquals(new Point(-2, 0), kicks[1]);
        assertEquals(new Point(1, 0), kicks[2]);
    }

    @Test
    @DisplayName("J/L/S/T/Z Kicks: State 0 to 3 (Counter-Clockwise)")
    void testGenericBrickKick0to3() {
        // SRS Rule for generic pieces 0->3 (Rotation to Left)
        // (0,0), (1,0), (1,1), (0,-2), (1,-2)
        Point[] kicks = SRSKickData.getKickOffsets(J_BRICK, 0, 3);

        assertEquals(5, kicks.length);
        assertEquals(new Point(0, 0), kicks[0]);
        assertEquals(new Point(1, 0), kicks[1]);
        assertEquals(new Point(1, 1), kicks[2]);
    }

    @Test
    @DisplayName("O-Brick should have no kicks")
    void testOBrickKicks() {
        // O Brick (Type 4 usually, or handled as default/separate)
        // Your code handles O_KICKS separately. Assuming type 4.
        Point[] kicks = SRSKickData.getKickOffsets(4, 0, 1);

        assertEquals(new Point(0, 0), kicks[0]);
        // O brick rotation usually doesn't need kicks, logic implies 0,0 return
    }
}