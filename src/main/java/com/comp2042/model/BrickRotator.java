package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;

/**
 * Helper class responsible for tracking and computing the rotation state
 * of a {@link Brick}.
 * <p>
 * It keeps track of the current rotation index and can provide information
 * about the next rotation state for preview or application on the board.
 */
public class BrickRotator {

    /**
     * The brick whose rotation states are being managed.
     */
    private Brick brick;

    /**
     * Index of the current rotation state within the brick's shape matrix list.
     */
    private int currentShape = 0;

    /**
     * Calculates information about the next rotation state of the current brick.
     * <p>
     * The index wraps around when the end of the rotation list is reached.
     *
     * @return a {@link NextShapeInfo} containing the next rotation matrix
     *         and its index within the brick's shape list
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Returns the matrix representing the current rotation state of the brick.
     *
     * @return a 2D {@code int} array for the current rotation
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation index for the brick.
     *
     * @param currentShape the index of the rotation state to use
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Assigns a new brick to this rotator and resets the rotation index.
     *
     * @param brick the {@link Brick} whose rotation states will be managed
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }


}
