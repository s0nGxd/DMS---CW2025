package com.comp2042.logic.bricks;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class LBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public LBrick() {
        // State 0: Spawn
        brickMatrix.add(new int[][]{
                {0, 0, 3},
                {3, 3, 3},
                {0, 0, 0},
        });

        // State 1: 90 degree
        brickMatrix.add(new int[][]{
                {0, 3, 0},
                {0, 3, 0},
                {0, 3, 3},
        });

        // State 2: 180 degree
        brickMatrix.add(new int[][]{
                {0, 0, 0},
                {3, 3, 3},
                {3, 0, 0},
        });

        // State 3: 270 degree
        brickMatrix.add(new int[][]{
                {3, 3, 0},
                {0, 3, 0},
                {0, 3, 0},
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}