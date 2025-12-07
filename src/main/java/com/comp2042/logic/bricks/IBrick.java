package com.comp2042.logic.bricks;

/**
 * Concrete implementation of the {@code I} tetromino.
 * <p>
 * Populates the {@link AbstractBrick#brickMatrix} with the two rotation
 * states: horizontal and vertical.
 */
final class IBrick extends AbstractBrick {

    /**
     * Creates a new {@code IBrick} and initialises its rotation matrices.
     * <p>
     * The first matrix represents the horizontal orientation, and the second
     * matrix represents the vertical orientation.
     */
    public IBrick() {
        brickMatrix.add(new int[][]{
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
            {0, 1, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 0, 0}
        });
    }
}
