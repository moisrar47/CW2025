package com.comp2042.logic.bricks;

/**
 * Concrete implementation of the {@code L} tetromino.
 * <p>
 * Populates the {@link AbstractBrick#brickMatrix} with all four rotation
 * states of the {@code L}-shaped brick.
 */
final class LBrick extends AbstractBrick {

    /**
     * Creates a new {@code LBrick} and initialises its rotation matrices.
     * <p>
     * The matrices define the {@code L} tetromino in each of its four
     * rotation states.
     */
    public LBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 3},
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 3, 0},
                {0, 0, 3, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 0, 3, 0},
                {3, 3, 3, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 3, 0, 0},
                {0, 3, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 0, 0}
        });
    }

}
