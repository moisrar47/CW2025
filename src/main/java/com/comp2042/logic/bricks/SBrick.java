package com.comp2042.logic.bricks;

/**
 * Concrete implementation of the {@code S} tetromino.
 * <p>
 * Populates the {@link AbstractBrick#brickMatrix} with the two rotation
 * states of the {@code S}-shaped brick.
 */
final class SBrick extends AbstractBrick {

    /**
     * Creates a new {@code SBrick} and initialises its rotation matrices.
     * <p>
     * The matrices define the {@code S} tetromino in its two rotation states.
     */
    public SBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
                {0, 0, 0, 0}
        });
    }

}
