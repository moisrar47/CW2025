package com.comp2042.model;

/**
 * Immutable value object describing the next rotation state of a brick.
 * <p>
 * Contains the rotation matrix for the next shape and the index of that
 * rotation within the brick's rotation list.
 */
public final class NextShapeInfo {

    /**
     * The rotation matrix representing the next shape state.
     */
    private final int[][] shape;

    /**
     * Index of this rotation within the brick's list of rotation states.
     */
    private final int position;

    /**
     * Creates a new {@code NextShapeInfo} container.
     *
     * @param shape    the rotation matrix for the next shape
     * @param position the index of this rotation in the brick's rotation list
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Returns a defensive copy of the rotation matrix for the next shape.
     * <p>
     * The returned matrix is safe to modify without affecting internal state.
     *
     * @return a deep copy of the next rotation matrix
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Returns the index of the next rotation position.
     *
     * @return the rotation index
     */
    public int getPosition() {
        return position;
    }
}
