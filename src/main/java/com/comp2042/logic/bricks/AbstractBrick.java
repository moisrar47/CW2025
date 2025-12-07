package com.comp2042.logic.bricks;

import com.comp2042.model.MatrixOperations;
import java.util.ArrayList;
import java.util.List;

/**
 * Base brick implementation that stores the rotation matrices for a tetromino
 * and provides a safe accessor that returns deep copies of the underlying data.
 * <p>
 * Concrete brick types extend this class and fill {@link #brickMatrix} with
 * their rotation states.
 */
abstract class AbstractBrick implements Brick {

    /**
     * Rotation matrices for this brick, one {@code int[][]} per rotation state.
     * Each matrix is copied when exposed via {@link #getShapeMatrix()} so external
     * callers cannot mutate the internal representation.
     */
    protected final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Returns deep copies of all rotation matrices for this brick.
     * <p>
     * The returned list and its {@code int[][]} elements can be safely modified
     * by callers without affecting the internal state of the brick.
     *
     * @return a list of {@code int[][]} matrices representing each rotation state
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
