package com.comp2042.logic.bricks;

/**
 * Concrete implementation of the {@code T} tetromino.
 * <p>
 * Populates the {@link AbstractBrick#brickMatrix} with all four rotation
 * states of the {@code T}-shaped brick.
 */
final class TBrick extends AbstractBrick {

    /**
     * Creates a new {@code TBrick} and initialises its rotation matrices.
     * <p>
     * The matrices define the {@code T} tetromino in each of its four
     * rotation states.
     */
    public TBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {6, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {0, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 6, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 0, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
    }

}
