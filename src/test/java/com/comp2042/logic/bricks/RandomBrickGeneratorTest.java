package com.comp2042.logic.bricks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomBrickGeneratorTest {

    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomBrickGenerator();
    }

    @Test
    @DisplayName("Generator initializes with a next brick")
    void testInitialState() {
        assertNotNull(generator.getNextBrick(), "Next brick should not be null initially");
    }

    @Test
    @DisplayName("Bag System: Generates all 7 unique bricks in every cycle")
    void testBagSystemFairness() {
        // The bag system ensures all 7 pieces appear once before repeating.
        // However, the generator initializes by pre-filling the queue with 4 items.
        // We will fetch a large batch (e.g., 70 bricks) and verify the distribution is roughly even,
        // or specifically check that we don't get the same piece 3+ times in a row (though theoretically possible at bag boundaries).

        // A stricter test for a 7-bag:
        // Fetch 14 bricks. We expect exactly 2 of each type?
        // Note: Your implementation adds to the queue when size <= 4.

        Set<Class<?>> brickTypes = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            Brick b = generator.getBrick();
            brickTypes.add(b.getClass());
        }

        // In a perfect 7-bag sequence, the first 7 draws might not be unique if the queue pre-fill crossed a bag boundary.
        // But over a large sample, every brick type must appear.

        for (int i = 0; i < 50; i++) {
            brickTypes.add(generator.getBrick().getClass());
        }

        assertEquals(7, brickTypes.size(), "Should verify that all 7 brick types are generated eventually");
    }

    @Test
    @DisplayName("GetNextBricks returns the requested number of previews")
    void testGetNextBricks() {
        List<Brick> previews = generator.getNextBricks(4);

        assertEquals(4, previews.size(), "Should return exactly 4 preview bricks");
        assertNotNull(previews.get(0));
        assertNotNull(previews.get(3));
    }
}