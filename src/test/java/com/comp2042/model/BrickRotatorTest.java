package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrickRotatorTest {

    // Simple test implementation of Brick with three distinct rotation states

    private static class TestBrick implements Brick {

        private final List<int[][]> shapes;

        TestBrick() {
            int[][] shape0 = {
                {1, 1},
                {0, 0}
            };
            int[][] shape1 = {
                {2, 2},
                {0, 0}
            };
            int[][] shape2 = {
                {3, 3},
                {0, 0}
            };
            shapes = Arrays.asList(shape0, shape1, shape2);
        }

        @Override
        public List<int[][]> getShapeMatrix() {
            return shapes;
        }
    }

    @Test
    void setBrick_resetsCurrentShapeToFirst() {
        BrickRotator rotator = new BrickRotator();
        Brick brick = new TestBrick();

        rotator.setBrick(brick);

        int[][] current = rotator.getCurrentShape();
        assertEquals(1, current[0][0], "First rotation state should start with value 1");
    }

    @Test
    void getNextShape_advancesAndWrapsAround() {
        BrickRotator rotator = new BrickRotator();
        Brick brick = new TestBrick();
        rotator.setBrick(brick);

        // First next shape from initial state (0) -> index 1
        NextShapeInfo next1 = rotator.getNextShape();
        assertEquals(1, next1.getPosition(), "First next position should be 1");
        assertEquals(2, next1.getShape()[0][0], "Second rotation state should start with value 2");

        // Emulate game behaviour: apply the rotation
        rotator.setCurrentShape(next1.getPosition());

        // Next from 1 -> index 2
        NextShapeInfo next2 = rotator.getNextShape();
        assertEquals(2, next2.getPosition(), "Next position should be 2");
        assertEquals(3, next2.getShape()[0][0], "Third rotation state should start with value 3");

        rotator.setCurrentShape(next2.getPosition());

        // Next from 2 -> wrap to 0
        NextShapeInfo next3 = rotator.getNextShape();
        assertEquals(0, next3.getPosition(), "Next position should wrap back to 0");
        assertEquals(1, next3.getShape()[0][0], "Wrapped rotation state should start with value 1");
    }
}
