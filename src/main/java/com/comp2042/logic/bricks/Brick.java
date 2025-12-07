package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Represents a Tetris brick (tetromino) that can provide its rotation shapes.
 * <p>
 * Implementations expose their rotation states as a list of {@code int[][]}
 * matrices, where each matrix corresponds to one rotation of the brick.
 */
public interface Brick {

    /**
     * Returns the rotation matrices that define this brick's shape.
     * <p>
     * Each {@code int[][]} in the list represents a single rotation state,
     * where non-zero entries indicate occupied cells.
     *
     * @return a list of matrices representing all rotation states of the brick
     */
    List<int[][]> getShapeMatrix();
}
