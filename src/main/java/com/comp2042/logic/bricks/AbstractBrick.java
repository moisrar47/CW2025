package com.comp2042.logic.bricks;

import com.comp2042.model.MatrixOperations;
import com.comp2042.logic.bricks.Brick;

import java.util.ArrayList;
import java.util.List;

/* Base brick implementation that stores the rotation matrices
  and provides a safe getter returning deep copies. */

abstract class AbstractBrick implements Brick {

    protected final List<int[][]> brickMatrix = new ArrayList<>();

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
