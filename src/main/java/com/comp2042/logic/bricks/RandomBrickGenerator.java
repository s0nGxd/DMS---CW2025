package com.comp2042.logic.bricks;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of BrickGenerator using the 7-bag randomization system.
 * Ensures fair distribution by cycling through all 7 brick types before repeating.
 */

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final Deque<Brick> bag = new ArrayDeque<>(); // Bag for 7-bag system

    // Show 4 future bricks
    private static final int NEXT_BRICKS_COUNT = 4;

    /**
     * Constructs a new RandomBrickGenerator and initializes the brick queue.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        // Generate 4 next bricks
        for (int i = 0; i < NEXT_BRICKS_COUNT; i++) {
            addBrickFromBag();
        }
    }

    /**
     * Adds a brick from the bag to the next bricks queue.
     * Refills the bag when empty using shuffled brick types.
     */
    // Method to handle the Bag System logic
    private void addBrickFromBag() {
        if (bag.isEmpty()) {
            // Refill bag with all 7 bricks and shuffle
            List<Brick> tempBag = new ArrayList<>(brickList);
            Collections.shuffle(tempBag);
            bag.addAll(tempBag);
        }
        // Move brick from bag to the queue
        nextBricks.add(bag.poll());
    }

    /**
     * Gets the next brick and advances the queue.
     * @return the next Brick to be played
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= NEXT_BRICKS_COUNT) {
            addBrickFromBag();
        }
        return nextBricks.poll();
    }

    /**
     * Previews the next brick without consuming it.
     * @return the Brick at the front of the queue
     */
    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /**
     * Gets a list of upcoming bricks for preview purposes.
     * @param count the number of future bricks to retrieve
     * @return list of upcoming Brick instances
     */
    // Method to add "future" bricks in a list
    @Override
    public List<Brick> getNextBricks(int count) {
        List<Brick> nextBricksList = new ArrayList<>();
        Iterator<Brick> iterator = nextBricks.iterator();

        for (int i = 0; i < count && iterator.hasNext(); i++) {
            nextBricksList.add(iterator.next());
        }

        return nextBricksList;
    }
}
