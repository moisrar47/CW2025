package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClearRowTest {

    @Test
    void constructorStoresValuesAndNewMatrixIsCopied() {
        int[][] original = {
            {0, 1},
            {2, 3}
        };

        ClearRow clearRow = new ClearRow(2, original, 200);

        // Basic fields are stored correctly
        assertEquals(2, clearRow.getLinesRemoved());
        assertEquals(200, clearRow.getScoreBonus());

        // Returned matrix content matches original initially
        int[][] fromGetter = clearRow.getNewMatrix();
        assertArrayEquals(original, fromGetter);

        // Mutate the returned array
        fromGetter[0][0] = 99;

        // Fresh call should not see that mutation (proves defensive copy)
        int[][] secondCall = clearRow.getNewMatrix();
        assertEquals(0, secondCall[0][0]);
    }
}
