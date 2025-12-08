package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Interface defining the contract for all Tetris brick types.
 * Each brick must provide its rotation states as a list of shape matrices.
 */

public interface Brick {

    /**
     * Gets all rotation states for this brick type.
     */
    List<int[][]> getShapeMatrix();
}
