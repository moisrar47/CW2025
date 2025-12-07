package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link BrickGenerator} implementation that produces random tetromino bricks.
 * <p>
 * Internally maintains a small queue of upcoming bricks so the current brick
 * and the "next piece" preview can be provided consistently.
 */
public class RandomBrickGenerator implements BrickGenerator {

    /**
     * All possible brick types that can be randomly selected.
     */
    private final List<Brick> brickList;

    /**
     * Queue of upcoming bricks. The head of the queue is the current brick
     * returned by {@link #getBrick()}, and the next element is used for
     * the "next piece" preview.
     */
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /**
     * Selects a random brick type from {@link #brickList}.
     *
     * @return a newly created random {@link Brick}
     */
    private Brick randomBrick() {
        return brickList.get(ThreadLocalRandom.current().nextInt(brickList.size()));
    }

    /**
     * Creates a new {@code RandomBrickGenerator} and initialises the list
     * of available brick types.
     * <p>
     * The queue of upcoming bricks is pre-populated so that both the current
     * and next brick are available immediately.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        nextBricks.add(randomBrick());
        nextBricks.add(randomBrick());
    }

    /**
     * Returns the current brick from the queue and advances the generator.
     * <p>
     * If the queue is running low, a new random brick is added to ensure
     * that a "next piece" is always available.
     *
     * @return the current {@link Brick} to be used in the game
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(randomBrick());
        }
        return nextBricks.poll();
    }

    /**
     * Returns the next brick in the queue without removing it.
     * <p>
     * Used by the GUI to display the "next piece" preview.
     *
     * @return the next {@link Brick}, or {@code null} if the queue is empty
     */
    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
