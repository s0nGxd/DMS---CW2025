package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Interface for generating random bricks during gameplay.
 * Implementations provides methods to get current and preview future bricks.
 */

public interface BrickGenerator {

    /**
     * Gets the next brick to be played.
     * @return a Brick instance representing the next piece
     */
    Brick getBrick();

    /**
     * Previews the upcoming brick without consuming it.
     * @return a Brick instance representing the next piece in queue
     */
    Brick getNextBrick();

    /**
     * Gets a list of upcoming bricks for preview.
     * @param count the number of future bricks to retrieve
     * @return list of upcoming Brick instances
     */
    List<Brick> getNextBricks(int count);
}
