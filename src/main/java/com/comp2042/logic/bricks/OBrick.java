package com.comp2042.logic.bricks;

import java.util.ArrayList;
import java.util.List;

final class OBrick extends AbstractBrick {

    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

}
