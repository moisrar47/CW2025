package com.comp2042.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing low-level matrix operations used by the Tetris board,
 * including collision checks, deep-copy helpers, row-clearing logic, and brick merging.
 * <p>
 * The class is non-instantiable and exposes only static operations.
 */

public class MatrixOperations {


    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MatrixOperations(){

    }

    /**
     * Checks whether placing the given brick at the specified coordinates
     * would result in a collision with either the board boundary or existing
     * filled cells.
     *
     * @param matrix the board matrix
     * @param brick  the brick rotation matrix to test
     * @param x      x-coordinate on the board
     * @param y      y-coordinate on the board
     * @return {@code true} if the brick intersects the boundary or an occupied cell,
     *         {@code false} otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines whether the given board coordinates lie outside the matrix bounds.
     *
     * @param matrix  the board matrix
     * @param targetX the x-coordinate to check
     * @param targetY the y-coordinate to check
     * @return {@code true} if the coordinate is outside the board, {@code false} otherwise
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        /* Out of bounds if:
         - X is negative
         - Y is negative
         - Y is beyond last row
         - X is beyond last column in that row */
        return targetX < 0
            || targetY < 0
            || targetY >= matrix.length
            || targetX >= matrix[targetY].length;
    }

    /**
     * Produces a deep copy of a 2D integer matrix.
     *
     * @param original the matrix to duplicate
     * @return a new matrix with identical values but independent references
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick into a background board matrix at the given coordinates.
     * <p>
     * A new matrix is returned; the original matrix is not modified.
     *
     * @param filledFields the board matrix
     * @param brick        the brick rotation matrix
     * @param x            x-coordinate where the brick is placed
     * @param y            y-coordinate where the brick is placed
     * @return a new matrix containing the merged brick values
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /**
     * Scans the board for fully completed rows, removes them, shifts remaining
     * rows downward, and computes the resulting score bonus.
     *
     * @param matrix the board matrix to examine
     * @return a {@link ClearRow} containing the number of removed rows,
     *         the updated board matrix, and the score bonus
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int rowCount = matrix.length;
        int columnCount = matrix[0].length;

        int[][] newMatrix = new int[rowCount][columnCount];
        Deque<int[]> remainingRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        // Identify full rows and collect the others
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int[] currentRow = matrix[rowIndex];
            int[] rowCopy = new int[currentRow.length];

            boolean isFullRow = true;
            for (int col = 0; col < currentRow.length; col++) {
                int cell = currentRow[col];
                if (cell == 0) {
                    isFullRow = false;
                }
                rowCopy[col] = cell;
            }

            if (isFullRow) {
                clearedRows.add(rowIndex);
            } else {
                remainingRows.add(rowCopy);
            }
        }

        // Rebuild matrix from bottom up with remaining rows
        for (int rowIndex = rowCount - 1; rowIndex >= 0; rowIndex--) {
            int[] row = remainingRows.pollLast();
            if (row == null) {
                break;
            }
            newMatrix[rowIndex] = row;
        }

        int clearedCount = clearedRows.size();
        int scoreBonus = 50 * clearedCount * clearedCount;

        return new ClearRow(clearedCount, newMatrix, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of 2D matrices.
     *
     * @param list the list of matrices to duplicate
     * @return a new list containing deep copies of the matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
