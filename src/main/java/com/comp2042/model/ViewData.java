package com.comp2042.model;

/**
 * Immutable data container used by the GUI to render the current game state.
 * <p>
 * Includes the active brick matrix, its board coordinates, the ghost-piece landing
 * row, and the next-piece preview data.
 */
public final class ViewData {

    /** Rotation matrix of the currently active brick. */
    private final int[][] brickData;

    /** X position of the active brick on the board. */
    private final int xPosition;

    /** Y position of the active brick on the board. */
    private final int yPosition;

    /** Y coordinate where the brick would land if dropped straight down (ghost piece). */
    private final int ghostYPosition;

    /** Rotation matrix of the next brick used for the preview panel. */
    private final int[][] nextBrickData;

    /**
     * Creates a new {@code ViewData} snapshot for GUI rendering.
     *
     * @param brickData      matrix for the active brick
     * @param xPosition      brick X coordinate
     * @param yPosition      brick Y coordinate
     * @param ghostYPosition landing row of the ghost piece
     * @param nextBrickData  matrix of the next brick for preview
     */
    public ViewData(int[][] brickData,
                    int xPosition,
                    int yPosition,
                    int ghostYPosition,
                    int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostYPosition = ghostYPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Returns a defensive copy of the active brick's matrix.
     *
     * @return the brick rotation matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Returns the X coordinate of the active brick.
     *
     * @return X position
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Returns the Y coordinate of the active brick.
     *
     * @return Y position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Returns the Y coordinate where the brick will land if dropped.
     *
     * @return ghost landing row
     */
    public int getGhostYPosition() {
        return ghostYPosition;
    }

    /**
     * Returns a defensive copy of the next brick's matrix for preview.
     *
     * @return next brick rotation matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
