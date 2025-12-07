package com.comp2042.model;

/**
 * Immutable value object representing a single high-score record.
 * <p>
 * Stores the player's name and their achieved score.
 */
public class HighScoreEntry {

    /**
     * Name of the player associated with this score.
     */
    private final String playerName;

    /**
     * Score value achieved by the player.
     */
    private final int score;

    /**
     * Creates a new high-score entry.
     *
     * @param playerName the name of the player
     * @param score      the score achieved by the player
     */
    public HighScoreEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    /**
     * Returns the name of the player.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the score for this entry.
     *
     * @return the score value
     */
    public int getScore() {
        return score;
    }
}
