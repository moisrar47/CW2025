package com.comp2042.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixOperations {


    //We don't want to instantiate this utility class
    private MatrixOperations(){

    }

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

    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
