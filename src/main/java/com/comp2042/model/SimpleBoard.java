package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

/**
 * Core implementation of the {@link Board} interface.
 * <p>
 * Manages the current falling brick, the game matrix, collision handling,
 * ghost-piece calculation, scoring, and creation of new bricks.
 */
public class SimpleBoard implements Board {

    /** Default X coordinate where new bricks spawn. */
    private static final int SPAWN_X = 6;

    /** Default Y coordinate where new bricks spawn. */
    private static final int SPAWN_Y = 1;

    /** Width of the board matrix (columns). */
    private final int width;

    /** Height of the board matrix (rows). */
    private final int height;

    /** Responsible for producing the next falling brick. */
    private final BrickGenerator brickGenerator;

    /** Handles rotation state for the active brick. */
    private final BrickRotator brickRotator;

    /** Background matrix of settled blocks. */
    private int[][] currentGameMatrix;

    /** Current X/Y position of the falling brick on the board. */
    private Point currentOffset;

    /** Score tracker for this board instance. */
    private final Score score;

    /**
     * Creates a new board of the given dimensions.
     *
     * @param width  width of the board matrix
     * @param height height of the board matrix
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the active brick by the given delta.
     *
     * @param deltaX horizontal movement offset
     * @param deltaY vertical movement offset
     * @return {@code true} if the move is valid, {@code false} if a collision occurs
     */
    private boolean tryMoveBrick(int deltaX, int deltaY) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point newOffset = new Point(currentOffset);
        newOffset.translate(deltaX, deltaY);
        boolean conflict = MatrixOperations.intersect(
            currentMatrix,
            brickRotator.getCurrentShape(),
            (int) newOffset.getX(),
            (int) newOffset.getY()
        );
        if (conflict) {
            return false;
        } else {
            currentOffset = newOffset;
            return true;
        }
    }

    /**
     * Computes the Y coordinate where the current brick would land if dropped
     * straight down from its current position (ghost piece).
     *
     * @return the Y coordinate of the ghost landing row
     */
    private int getGhostYPosition() {
        int x = (int) currentOffset.getX();
        int y = (int) currentOffset.getY();

        int[][] shape = brickRotator.getCurrentShape();

        int ghostY = y;

        while (true) {
            int nextY = ghostY + 1;
            boolean conflict = MatrixOperations.intersect(
                currentGameMatrix,
                shape,
                x,
                nextY
            );
            if (conflict) {
                break; // last non conflicting position is the landing row
            }
            ghostY = nextY;
        }

        return ghostY;
    }

    /**
     * Moves the active brick down by one row.
     *
     * @return {@code true} if the movement was successful, {@code false} otherwise
     */
    @Override
    public boolean moveBrickDown() {
        return tryMoveBrick(0, 1);
    }

    /**
     * Moves the active brick left by one column.
     *
     * @return {@code true} if the movement was valid
     */
    @Override
    public boolean moveBrickLeft() {
        return tryMoveBrick(-1, 0);
    }

    /**
     * Moves the active brick right by one column.
     *
     * @return {@code true} if the movement was valid
     */
    @Override
    public boolean moveBrickRight() {
        return tryMoveBrick(1, 0);
    }

    /**
     * Attempts to rotate the active brick counter-clockwise.
     *
     * @return {@code true} if the rotation is allowed, {@code false} otherwise
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    /**
     * Spawns a new brick at the default spawn coordinates.
     *
     * @return {@code true} if the brick immediately collides (game over),
     *         {@code false} otherwise
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(SPAWN_X, SPAWN_Y);

        boolean intersectsExistingBlocks = MatrixOperations.intersect(
            currentGameMatrix,
            brickRotator.getCurrentShape(),
            (int) currentOffset.getX(),
            (int) currentOffset.getY()
        );

        // true here means we collided immediately hence game over
        return intersectsExistingBlocks;
    }

    /**
     * Returns the current background board matrix (settled blocks).
     *
     * @return the board matrix
     */
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Produces a {@link ViewData} snapshot used by the GUI to render the board,
     * including the ghost piece and next-piece preview.
     *
     * @return the view data describing the current state of the game
     */
    @Override
    public ViewData getViewData() {
        int ghostY = getGhostYPosition();
        return new ViewData(
            brickRotator.getCurrentShape(),
            (int) currentOffset.getX(),
            (int) currentOffset.getY(),
            ghostY,
            brickGenerator.getNextBrick().getShapeMatrix().get(0)
        );
    }

    /**
     * Merges the active brick permanently into the background matrix.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Clears any completed rows from the board.
     *
     * @return a {@link ClearRow} describing removed lines and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    /**
     * Returns the score tracker for this board.
     *
     * @return the {@link Score} instance
     */
    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Resets the board to an empty state, resets the score,
     * and spawns a new brick.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}
