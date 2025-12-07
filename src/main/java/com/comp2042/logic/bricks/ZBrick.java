package com.comp2042.logic.bricks;

/**
 * Concrete implementation of the {@code Z} tetromino.
 * <p>
 * Populates the {@link AbstractBrick#brickMatrix} with the two rotation
 * states of the {@code Z}-shaped brick.
 */
final class ZBrick extends AbstractBrick {

    /**
     * Creates a new {@code ZBrick} and initialises its rotation matrices.
     * <p>
     * The matrices define the {@code Z} tetromino in its two rotation states.
     */
    public ZBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
        });
    }

}
