package com.comp2042.model;

/**
 * Immutable value object representing the result of a row-clearing operation.
 * <p>
 * It records how many lines were removed, the updated board matrix after the
 * clear, and the score bonus awarded for the operation.
 */
public final class ClearRow {

    /**
     * Number of completed lines that were removed from the board.
     */
    private final int linesRemoved;

    /**
     * Board matrix after the rows have been cleared.
     */
    private final int[][] newMatrix;

    /**
     * Score bonus awarded for clearing the lines.
     */
    private final int scoreBonus;

    /**
     * Creates a new {@code ClearRow} result.
     *
     * @param linesRemoved the number of lines that were cleared
     * @param newMatrix    the board matrix after the clear operation
     * @param scoreBonus   the score awarded for clearing these lines
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Returns the number of lines that were removed.
     *
     * @return the number of cleared lines
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Returns a defensive copy of the updated board matrix.
     * <p>
     * Callers can safely modify the returned array without affecting the
     * internal state of this {@code ClearRow} instance.
     *
     * @return a copy of the board matrix after the clear
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Returns the score bonus awarded for clearing the lines.
     *
     * @return the bonus score value
     */

    public int getScoreBonus() {
        return scoreBonus;
    }
}
