package com.comp2042.logic.bricks;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final Deque<Brick> bag = new ArrayDeque<>(); // Bag for 7-bag system

    // Show 4 future bricks
    private static final int NEXT_BRICKS_COUNT = 4;

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

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= NEXT_BRICKS_COUNT) {
            addBrickFromBag();
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

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
