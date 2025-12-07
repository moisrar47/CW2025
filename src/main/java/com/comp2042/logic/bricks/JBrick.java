package com.comp2042.logic.bricks;

/**
 * Concrete implementation of the {@code J} tetromino.
 * <p>
 * Populates the {@link AbstractBrick#brickMatrix} with all four rotation
 * states of the {@code J}-shaped brick.
 */
final class JBrick extends AbstractBrick {

    /**
     * Creates a new {@code JBrick} and initialises its rotation matrices.
     * <p>
     * The matrices define the {@code J} tetromino in each of its four
     * rotation states.
     */
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
