package com.comp2042;

import java.awt.Point;

/**
 Super Rotation System (SRS) wall kick data

 Performs positional rotations and wall kicks for when the blocks are at the edge

 References:
  - https://tetris.wiki/Super_Rotation_System
  - https://harddrop.com/wiki/SRS
  - tetr.io
 */

public class SRSKickData {
    /**
     * Format: [from_state][direction][test_number]
     * - from_state: Current rotation state (0-3)
     * - direction: 0 = clockwise, 1 = counter-clockwise
     * - test_number: Which kick to try (0-4, in order of priority)
     */

    // Kick offsets for J, L, S, T, Z Bricks
    private static final Point[][][] JLSTZ_KICKS = {
            // From state 0
            {
                    // 0->1 (clockwise to R)
                    { new Point(0, 0), new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) },
                    // 0->3 (counter-clockwise to L)
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) }
            },
            // From state 1
            {
                    // 1->2 (clockwise to 2)
                    { new Point(0, 0), new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) },
                    // 1->0 (counter-clockwise to 0)
                    { new Point(0, 0), new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) }
            },
            // From state 2
            {
                    // 2->3 (clockwise to L)
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) },
                    // 2->1 (counter-clockwise to R)
                    { new Point(0, 0), new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) }
            },
            // From state 3
            {
                    // 3->0 (clockwise to 0)
                    { new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) },
                    // 3->2 (counter-clockwise to 2)
                    { new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) }
            }
    };

    // Kick offsets for the I-Brick
    private static final Point[][][] I_KICKS = {
            // From state 0
            {
                    // 0->1 (clockwise)
                    { new Point(0, 0), new Point(-2, 0), new Point(1, 0), new Point(-2, -1), new Point(1, 2) },
                    // 0->3 (counter-clockwise)
                    { new Point(0, 0), new Point(-1, 0), new Point(2, 0), new Point(-1, 2), new Point(2, -1) }
            },
            // From state 1
            {
                    // 1->2 (clockwise)
                    { new Point(0, 0), new Point(-1, 0), new Point(2, 0), new Point(-1, 2), new Point(2, -1) },
                    // 1->0 (counter-clockwise)
                    { new Point(0, 0), new Point(2, 0), new Point(-1, 0), new Point(2, 1), new Point(-1, -2) }
            },
            // From state 2
            {
                    // 2->3 (clockwise)
                    { new Point(0, 0), new Point(2, 0), new Point(-1, 0), new Point(2, 1), new Point(-1, -2) },
                    // 2->1 (counter-clockwise)
                    { new Point(0, 0), new Point(1, 0), new Point(-2, 0), new Point(1, -2), new Point(-2, 1) }
            },
            // From state 3
            {
                    // 3->0 (clockwise)
                    { new Point(0, 0), new Point(1, 0), new Point(-2, 0), new Point(1, -2), new Point(-2, 1) },
                    // 3->2 (counter-clockwise)
                    { new Point(0, 0), new Point(-2, 0), new Point(1, 0), new Point(-2, -1), new Point(1, 2) }
            }
    };

    // Kick offset for O-Brick
    private static final Point[][][] O_KICKS = {
            { { new Point(0, 0) }, { new Point(0, 0) } },
            { { new Point(0, 0) }, { new Point(0, 0) } },
            { { new Point(0, 0) }, { new Point(0, 0) } },
            { { new Point(0, 0) }, { new Point(0, 0) } }
    };

    /**
     Determine Kick Offset base on Specific Rotation
     Determine Brick Type through colour value, Rotational State and The next Rotational State
     */
    public static Point[] getKickOffsets(int brickType, int fromState, int toState) {
        // Determine rotation direction
        int diff = (toState - fromState + 4) % 4;
        boolean isClockwise = (diff == 1);
        int direction = isClockwise ? 0 : 1;

        // Select appropriate kick table
        Point[][][] kickTable;
        if (brickType == 1) {
            kickTable = I_KICKS;
        } else if (brickType == 4) {
            kickTable = O_KICKS;
        } else {
            kickTable = JLSTZ_KICKS;
        }
        // Return kick offset in priority order
        return kickTable[fromState][direction];
    }
}