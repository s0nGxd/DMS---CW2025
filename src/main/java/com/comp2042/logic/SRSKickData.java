package com.comp2042.logic;

import java.awt.Point;

/**
 * Contains Super Rotation System (SRS) wall kick offset data tables.
 *
 * <p>SRS is the official Tetris rotation system that allows pieces to "kick"
 * off walls and other blocks when rotating. This class stores the precise
 * offset data for all kick tests, organized by brick type and rotation direction.</p>
 *
 * <p>Three kick tables are defined:
 * - JLSTZ_KICKS: For J, L, S, T, and Z pieces
 * - I_KICKS: Special table for I piece (horizontal/vertical transitions)
 * - O_KICKS: No rotation (O piece doesn't rotate)</p>
 *
 * <p>References:
 * - https://tetris.wiki/Super_Rotation_System
 * - https://harddrop.com/wiki/SRS
 * - https://tetris.fandom.com/wiki/Super_Rotation_System</p>
 * - tetr.io
 */

public class SRSKickData {
    /**
     * Kick data table for JLSTZ Bricks
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

    /**
     * Kick data table for I Brick
     * - from_state: Current rotation state (0-3)
     * - direction: 0 = clockwise, 1 = counter-clockwise
     * - test_number: Which kick to try (0-4, in order of priority)
     */
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

    /**
     * Kick data table for O Brick
     * - from_state: Current rotation state (0-3)
     * - direction: 0 = clockwise, 1 = counter-clockwise
     * - test_number: Which kick to try (0-4, in order of priority)
     */
    // Kick offset for O-Brick
    private static final Point[][][] O_KICKS = {
            { { new Point(0, 0) }, { new Point(0, 0) } },
            { { new Point(0, 0) }, { new Point(0, 0) } },
            { { new Point(0, 0) }, { new Point(0, 0) } },
            { { new Point(0, 0) }, { new Point(0, 0) } }
    };

    /**
     * Returns a specific wall kick to use
     *
     * <p>Determines which kick table to use based on brick type (I=1, O=4, others=JLSTZ),
     * calculates rotation direction (clockwise vs counter-clockwise), and returns
     * the appropriate array of kick offsets in priority order.</p>
     *
     * <p>Kick offsets are tested in order until one succeeds or all fail. Each offset
     * is an (x,y) displacement to try for the rotated piece.</p>
     *
     * @param brickType brick color ID (1=I, 2=J, 3=L, 4=O, 5=S, 6=T, 7=Z)
     * @param fromState current rotation state (0-3)
     * @param toState target rotation state (0-3)
     * @return array of Point offsets to test in priority order
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