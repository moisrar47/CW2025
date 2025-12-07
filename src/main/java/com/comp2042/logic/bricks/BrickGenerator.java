package com.comp2042.logic.bricks;

/**
 * Strategy interface for generating Tetris bricks.
 * <p>
 * Implementations decide how new bricks are produced (for example,
 * completely random or using a preview/queue system).
 */
public interface BrickGenerator {

    /**
     * Returns the current brick that should be active in the game.
     * <p>
     * This is typically the brick that the player is currently controlling
     * on the board.
     *
     * @return the current {@link Brick}
     */
    Brick getBrick();

    /**
     * Returns the next brick that will follow the current one.
     * <p>
     * This value is used by the GUI to show the "next piece" preview.
     *
     * @return the next {@link Brick} that will be spawned
     */
    Brick getNextBrick();
}
