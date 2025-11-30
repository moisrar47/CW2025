package com.comp2042;

import com.comp2042.model.ClearRow;
import com.comp2042.model.MatrixOperations;
import com.comp2042.model.Score;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.model.ViewData;

import java.awt.*;

public class SimpleBoard implements Board {

    private static final int SPAWN_X = 4;
    private static final int SPAWN_Y = 10;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

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

    @Override
    public boolean moveBrickDown() {
        return tryMoveBrick(0, 1);
    }

    @Override
    public boolean moveBrickLeft() {
        return tryMoveBrick(-1, 0);
    }

    @Override
    public boolean moveBrickRight() {
        return tryMoveBrick(1, 0);
    }


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

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}
