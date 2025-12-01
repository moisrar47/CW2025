package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {

    @Test
    void intersect_returnsFalse_whenBrickDoesNotOverlapAndIsInBounds() {
        int[][] board = new int[4][4];
        int[][] brick = {
            {1}
        };

        boolean result = MatrixOperations.intersect(board, brick, 1, 1);

        assertFalse(result, "Brick in empty space should not intersect");
    }

    @Test
    void intersect_returnsTrue_whenBrickOverlapsFilledCell() {
        int[][] board = new int[4][4];
        board[2][2] = 9;

        int[][] brick = {
            {1}
        };

        boolean result = MatrixOperations.intersect(board, brick, 2, 2);

        assertTrue(result, "Brick placed on an occupied cell should intersect");
    }

    @Test
    void intersect_returnsTrue_whenBrickIsOutOfBounds() {
        int[][] board = new int[4][4];
        int[][] brick = {
            {1}
        };

        boolean result = MatrixOperations.intersect(board, brick, -1, 0);

        assertTrue(result, "Brick partially outside the board should be treated as intersecting");
    }

    @Test
    void copy_createsIndependentDeepCopy() {
        int[][] original = {
            {1, 2},
            {3, 4}
        };

        int[][] copy = MatrixOperations.copy(original);

        assertArrayEquals(original[0], copy[0]);
        assertArrayEquals(original[1], copy[1]);

        // mutate copy and ensure original is unchanged
        copy[0][0] = 99;

        assertEquals(1, original[0][0], "Original matrix should not be affected by changes in the copy");
    }

    @Test
    void merge_placesBrickValuesIntoNewMatrix() {
        int[][] board = new int[4][4];
        int[][] brick = {
            {0, 2},
            {0, 2}
        };

        int[][] result = MatrixOperations.merge(board, brick, 1, 1);

        // brick[j][i] mapping means brick is applied at (x + i, y + j)
        assertEquals(2, result[1][2]);
        assertEquals(2, result[2][2]);

        // original board should remain unchanged
        assertEquals(0, board[1][2]);
        assertEquals(0, board[2][2]);
    }

    @Test
    void checkRemoving_returnsZeroWhenNoFullRows() {
        int[][] board = {
            {0, 0, 0, 0},
            {1, 0, 1, 0},
            {0, 2, 0, 2},
            {3, 0, 3, 0}
        };

        ClearRow clearRow = MatrixOperations.checkRemoving(board);

        assertEquals(0, clearRow.getLinesRemoved(), "No row is completely filled, so nothing should be removed");
        assertEquals(0, clearRow.getScoreBonus(), "No cleared rows should give zero bonus");
        assertArrayEquals(board, clearRow.getNewMatrix(), "Matrix should remain unchanged when nothing is cleared");
    }

    @Test
    void checkRemoving_shiftsRowsDownAndComputesScore_forSingleFullRow() {
        int[][] board = {
            {0, 0, 0, 0},
            {1, 1, 1, 1}, // full row
            {0, 2, 0, 2},
            {3, 3, 3, 3}  // full row at bottom
        };

        ClearRow clearRow = MatrixOperations.checkRemoving(board);

        int lines = clearRow.getLinesRemoved();
        int[][] newMatrix = clearRow.getNewMatrix();

        assertEquals(2, lines, "Two full rows should be removed");
        assertEquals(50 * lines * lines, clearRow.getScoreBonus(), "Score bonus formula should be 50 * n^2");

        // Remaining non-empty rows (0 and 2) should be at the bottom of the new matrix
        assertArrayEquals(board[0], newMatrix[2]);
        assertArrayEquals(board[2], newMatrix[3]);
    }

    @Test
    void deepCopyList_createsIndependentCopiesOfAllMatrices() {
        int[][] m1 = {
            {1, 0},
            {0, 1}
        };
        int[][] m2 = {
            {2, 2},
            {2, 2}
        };

        List<int[][]> original = List.of(m1, m2);

        List<int[][]> copy = MatrixOperations.deepCopyList(original);

        assertEquals(2, copy.size());
        assertArrayEquals(m1, copy.get(0));
        assertArrayEquals(m2, copy.get(1));

        copy.get(0)[0][0] = 99;

        assertEquals(1, m1[0][0], "Original list matrices should not change when copies are mutated");
    }
}
