package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleBoardTest {

    @Test
    void newGame_clearsBoardAndResetsScore() {
        // use a small board to keep the test simple
        SimpleBoard board = new SimpleBoard(4, 4);

        // Arrange: dirty the board and bump the score
        int[][] matrixBefore = board.getBoardMatrix();
        matrixBefore[0][0] = 1;
        matrixBefore[1][1] = 2;
        board.getScore().add(100);

        // Act
        board.newGame();

        // Assert: board matrix is cleared
        int[][] matrixAfter = board.getBoardMatrix();
        for (int y = 0; y < matrixAfter.length; y++) {
            for (int x = 0; x < matrixAfter[y].length; x++) {
                assertEquals(0, matrixAfter[y][x], "Board should be cleared after newGame()");
            }
        }

        // Assert: score reset to zero
        assertEquals(0, board.getScore().scoreProperty().get(), "Score should be reset after newGame()");
    }
}
