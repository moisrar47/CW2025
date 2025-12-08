package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Encapsulates the Tetris level progression and line-tracking logic.
 * <p>
 * Rules (current behaviour):
 * Level 1 -> 2: 3 lines
 * Level 2 -> 3: +5 lines
 * Level 3 -> 4: +7 lines
 * Each subsequent level increases the requirement by 2 additional lines.
 */
public class LevelManager {

    // current level (backing the HUD "Level" label)
    private final IntegerProperty levelProperty = new SimpleIntegerProperty(1);

    // total lines cleared across the current game (HUD "Lines")
    private final IntegerProperty linesClearedProperty = new SimpleIntegerProperty(0);

    // how many more lines are needed to reach the next level
    // Level 1 -> 2 starts at 3
    private int linesRemainingForNextLevel = 3;

    /**
     * Creates a new {@code LevelManager} and initialises it to
     * level 1 with zero lines cleared.
     */
    public LevelManager() {
        reset();
    }

    /**
     * Resets level progression for a fresh game.
     * <p>
     * Sets level to 1, total lines cleared to 0, and restores the
     * initial requirement of 3 lines for level 1 -> 2.
     */
    public void reset() {
        levelProperty.set(1);
        linesClearedProperty.set(0);
        linesRemainingForNextLevel = 3; // 1 -> 2
    }

    // Property accessors for HUD binding

    /**
     * Returns the JavaFX property representing the current level.
     *
     * @return the {@link IntegerProperty} backing the HUD "Level" label
     */
    public IntegerProperty levelProperty() {
        return levelProperty;
    }

    /**
     * Returns the JavaFX property representing the total lines cleared.
     *
     * @return the {@link IntegerProperty} backing the HUD "Lines" label
     */
    public IntegerProperty linesClearedProperty() {
        return linesClearedProperty;
    }

    /**
     * Returns the current level value.
     *
     * @return the current level
     */
    public int getLevel() {
        return levelProperty.get();
    }

    /**
     * Returns the total number of lines cleared in the current game.
     *
     * @return total lines cleared
     */
    public int getTotalLinesCleared() {
        return linesClearedProperty.get();
    }

    /**
     * Applies the line-clear progression rules for the given number of removed lines.
     * <p>
     * Updates the total lines cleared, consumes them towards the next level
     * threshold, and may increase the level one or more times.
     *
     * @param linesRemoved how many lines were cleared in this move
     * @return {@code true} if at least one level-up occurred, {@code false} otherwise
     */
    public boolean applyLineClearProgression(int linesRemoved) {
        if (linesRemoved <= 0) {
            return false;
        }

        // 1) update the total lines cleared (HUD "Lines")
        int total = linesClearedProperty.get() + linesRemoved;
        linesClearedProperty.set(total);

        // 2) Consume those lines towards the next level, possibly levelling up
        int remaining = linesRemainingForNextLevel;
        int toConsume = linesRemoved;
        int currentLevel = levelProperty.get();
        boolean leveledUp = false;

        while (toConsume > 0) {
            if (toConsume >= remaining) {
                // reach the next level
                toConsume -= remaining;
                currentLevel++;
                levelProperty.set(currentLevel);
                leveledUp = true;

                // requirement for the *next* level:
                // for level N -> N+1: 3 + 2*(N-1)
                int requiredForNext = 3 + 2 * (currentLevel - 1);
                remaining = requiredForNext;
            } else {
                // stay on the same level, just reduce remaining
                remaining -= toConsume;
                toConsume = 0;
            }
        }

        linesRemainingForNextLevel = remaining;
        return leveledUp;
    }
}
