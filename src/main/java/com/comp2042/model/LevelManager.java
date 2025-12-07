package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/*
  Encapsulates the Tetris level progression and line-tracking logic.

  Rules (current behaviour):
    Level 1 -> 2: 3 lines
    Level 2 -> 3: +5 lines
   Level 3 -> 4: +7 lines
  each step, the requirement increases by 2 lines.
 */
public class LevelManager {

    // current level (backing the HUD "Level" label)
    private final IntegerProperty levelProperty = new SimpleIntegerProperty(1);

    // total lines cleared across the current game (HUD "Lines")
    private final IntegerProperty linesClearedProperty = new SimpleIntegerProperty(0);

    // how many more lines are needed to reach the next level
    // Level 1 -> 2 starts at 3
    private int linesRemainingForNextLevel = 3;

    public LevelManager() {
        reset();
    }

    // Resets level progression for a fresh game.
    public void reset() {
        levelProperty.set(1);
        linesClearedProperty.set(0);
        linesRemainingForNextLevel = 3; // 1 -> 2
    }

    // Property accessors for HUD binding
    public IntegerProperty levelProperty() {
        return levelProperty;
    }

    public IntegerProperty linesClearedProperty() {
        return linesClearedProperty;
    }

    public int getLevel() {
        return levelProperty.get();
    }

    public int getTotalLinesCleared() {
        return linesClearedProperty.get();
    }

    /*
      Apply line-clear progression rules.

      @param linesRemoved how many lines were cleared in this move
      @return true if at least one level-up occurred
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
