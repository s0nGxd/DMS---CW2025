package com.comp2042.logic;

import com.comp2042.data.NextShapeInfo;
import com.comp2042.logic.bricks.Brick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrickRotatorTest {

    private BrickRotator rotator;

    // Simple Mock Brick for testing
    private static class MockBrick implements Brick {
        @Override
        public List<int[][]> getShapeMatrix() {
            List<int[][]> shapes = new ArrayList<>();
            shapes.add(new int[][]{{1}}); // State 0
            shapes.add(new int[][]{{2}}); // State 1
            shapes.add(new int[][]{{3}}); // State 2
            shapes.add(new int[][]{{4}}); // State 3
            return shapes;
        }
    }

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        rotator.setBrick(new MockBrick());
    }

    @Test
    @DisplayName("Initial state is 0")
    void testInitialState() {
        assertEquals(0, rotator.getCurrentState());
        assertEquals(1, rotator.getCurrentShape()[0][0]); // Value from Mock State 0
    }

    @Test
    @DisplayName("Clockwise rotation cycles correctly (0 -> 1 -> 2 -> 3 -> 0)")
    void testClockwiseCycle() {
        NextShapeInfo next = rotator.getNextShapeClockwise();
        assertEquals(1, next.getPosition(), "Should rotate to state 1");

        rotator.setCurrentShape(1);
        next = rotator.getNextShapeClockwise();
        assertEquals(2, next.getPosition(), "Should rotate to state 2");

        rotator.setCurrentShape(3);
        next = rotator.getNextShapeClockwise();
        assertEquals(0, next.getPosition(), "Should wrap around to state 0");
    }

    @Test
    @DisplayName("Counter-Clockwise rotation cycles correctly (0 -> 3 -> 2)")
    void testCounterClockwiseCycle() {
        NextShapeInfo next = rotator.getNextShapeCounterClockwise();
        assertEquals(3, next.getPosition(), "Should rotate backwards to state 3");

        rotator.setCurrentShape(3);
        next = rotator.getNextShapeCounterClockwise();
        assertEquals(2, next.getPosition(), "Should rotate backwards to state 2");
    }
}