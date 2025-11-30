package com.comp2042.logic.bricks;

final class JBrick extends AbstractBrick {

    public JBrick() {
        brickMatrix.add(new int[][]{
            {0, 0, 0, 0},
            {2, 2, 2, 0},
            {0, 0, 2, 0},
            {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
            {0, 0, 0, 0},
            {0, 2, 2, 0},
            {0, 2, 0, 0},
            {0, 2, 0, 0}
        });
        brickMatrix.add(new int[][]{
            {0, 0, 0, 0},
            {0, 2, 0, 0},
            {0, 2, 2, 2},
            {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
            {0, 0, 2, 0},
            {0, 0, 2, 0},
            {0, 2, 2, 0},
            {0, 0, 0, 0}
        });
    }
}
