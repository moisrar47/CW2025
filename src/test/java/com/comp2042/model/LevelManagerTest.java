package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelManagerTest {

    @Test
    void startsAtLevelOneWithZeroLines() {
        LevelManager levelManager = new LevelManager();

        assertEquals(1, levelManager.getLevel());
        assertEquals(0, levelManager.getTotalLinesCleared());
    }

    @Test
    void levelsUpAfterClearingRequiredLines() {
        LevelManager levelManager = new LevelManager();

        // Level 1 -> 2 requires 3 lines
        boolean leveledUp = levelManager.applyLineClearProgression(3);

        assertTrue(leveledUp, "Should level up after 3 lines from level 1");
        assertEquals(2, levelManager.getLevel());
        assertEquals(3, levelManager.getTotalLinesCleared());
    }

    @Test
    void canHandleMultipleLevelUpsAcrossCalls() {
        LevelManager levelManager = new LevelManager();

        // 1 -> 2: 3 lines
        levelManager.applyLineClearProgression(3);
        assertEquals(2, levelManager.getLevel());

        // 2 -> 3: +5 lines required
        boolean leveledUpTo3 = levelManager.applyLineClearProgression(5);

        assertTrue(leveledUpTo3);
        assertEquals(3, levelManager.getLevel());
        assertEquals(8, levelManager.getTotalLinesCleared());
    }
}
