package com.comp2042.constant;

public final class GameConstants {

    private GameConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    // UI Constants
    public static final int BRICK_SIZE = 20;
    public static final int BRICK_ARC_SIZE = 9;
    public static final double GHOST_OPACITY = 0.3;

    // Board Dimensions
    public static final int BOARD_WIDTH = 25;
    public static final int BOARD_HEIGHT = 10;
    public static final int VISIBLE_ROWS_START = 2;

    // Timing
    public static final int DEFAULT_FALL_SPEED = 400;

    // Game Modes
    public static final int SPRINT_TARGET_LINES = 40;
    public static final long BLITZ_TIME_LIMIT = 180000;

    // Layout
    public static final int BRICK_Y_OFFSET = -42;
}
