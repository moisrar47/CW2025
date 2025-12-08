package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Tracks the player's score using a JavaFX {@link IntegerProperty} so that
 * the GUI can bind directly to score updates.
 */
public final class Score {

    // JavaFX property representing the current score value
    // Enables automatic GUI updates via property binding
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Returns the JavaFX property storing the score.
     * <p>
     * Used by the GUI for real-time HUD binding.
     *
     * @return the observable score property
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds the specified amount to the current score.
     *
     * @param i the number of points to add to the score
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score back to zero.
     */
    public void reset() {
        score.setValue(0);
    }
}
