package com.comp2042.logic.bricks;

/**
 * Concrete implementation of the {@code O} tetromino.
 * <p>
 * Populates the {@link AbstractBrick#brickMatrix} with the square shape,
 * which looks the same in all rotation states.
 */
final class OBrick extends AbstractBrick {

    /**
     * Creates a new {@code OBrick} and initialises its single rotation matrix.
     * <p>
     * The {@code O} tetromino is symmetrical, so only one rotation state
     * is required.
     */
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

}
