package com.comp2042.model;

/**
 * Represents the game board model that stores the active brick and the
 * background grid of settled bricks.
 * <p>
 * Implementations of this interface provide operations for moving and
 * rotating the active brick, creating new bricks, clearing full rows,
 * tracking the score, and exposing a {@link ViewData} snapshot for rendering.
 */
public interface Board {

    /**
     * Attempts to move the active brick one row down.
     *
     * @return {@code true} if the brick was moved down successfully,
     *         {@code false} if it cannot move further (for example, it has landed)
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the active brick one column to the left.
     *
     * @return {@code true} if the brick was moved left successfully,
     *         {@code false} if the movement is blocked
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the active brick one column to the right.
     *
     * @return {@code true} if the brick was moved right successfully,
     *         {@code false} if the movement is blocked
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the active brick to the left (counter-clockwise).
     *
     * @return {@code true} if the rotation was applied successfully,
     *         {@code false} if the rotation would place the brick in an invalid position
     */
    boolean rotateLeftBrick();

    /**
     * Creates a new active brick on the board.
     *
     * @return {@code true} if the new brick was created successfully,
     *         {@code false} if there is no space for a new brick (game over condition)
     */
    boolean createNewBrick();

    /**
     * Returns the current state of the board background grid.
     *
     * @return a 2D {@code int} array representing the settled bricks on the board
     */
    int[][] getBoardMatrix();

    /**
     * Returns a view-friendly snapshot of the current game state.
     *
     * @return a {@link ViewData} instance that can be used by the GUI to render the board
     */
    ViewData getViewData();

    /**
     * Merges the active brick into the background grid so that it becomes part
     * of the settled board state.
     */
    void mergeBrickToBackground();

    /**
     * Clears any full rows from the board and calculates the resulting score bonus.
     *
     * @return a {@link ClearRow} object describing which lines were removed and
     *         how many points should be awarded
     */
    ClearRow clearRows();

    /**
     * Returns the score object associated with this board.
     *
     * @return the {@link Score} instance tracking the current game score
     */
    Score getScore();

    /**
     * Resets the board to a fresh game state, clearing any existing bricks and score.
     */
    void newGame();
}
