package com.comp2042.controller;

import com.comp2042.model.Board;
import com.comp2042.model.ClearRow;
import com.comp2042.input.EventSource;
import com.comp2042.input.InputEventListener;
import com.comp2042.input.MoveEvent;
import com.comp2042.model.SimpleBoard;
import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
import com.comp2042.view.GuiController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class GameController implements InputEventListener {

    // 2 hidden rows, 20 visible = 22
    private static final int BOARD_ROWS = 22;
    private static final int BOARD_COLUMNS = 10;

    private final Board board = new SimpleBoard(BOARD_ROWS, BOARD_COLUMNS);

    private final GuiController viewGuiController;

    // level / lines tracking for HUD
    // current level shown in the HUD
    private final IntegerProperty levelProperty = new SimpleIntegerProperty(1);

    // total lines cleared across the current game
    private final IntegerProperty linesClearedProperty = new SimpleIntegerProperty(0);

    // how many more lines are needed to reach the *next* level
    // level 1 -> 2: start with 3
    private int linesRemainingForNextLevel = 3;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        // binding new HUD elements here
        viewGuiController.bindLevel(levelProperty);
        viewGuiController.bindLines(linesClearedProperty);

    }

    private ClearRow handleBrickLanded() {
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());

            applyLineClearProgression(clearRow.getLinesRemoved());
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        return clearRow;
    }

    /*
     * This applies the level progression rule whenever some lines are cleared.
     *
     * Level thresholds:
     *   Level 1 -> 2: 3 lines
     *   Level 2 -> 3: +5 lines
     *   Level 3 -> 4: +7 lines
     *
     * Each step the requirement increases by 2.
     */
    private void applyLineClearProgression(int linesRemoved) {
        if (linesRemoved <= 0) {
            return;
        }

        // 1) update the total lines cleared (backing the HUD 'Lines' label)
        int total = linesClearedProperty.get() + linesRemoved;
        linesClearedProperty.set(total);

        // 2) apply those lines against the "linesRemainingForNextLevel" bucket,
        //    levelling up as many times as needed
        int remaining = linesRemainingForNextLevel;
        int toConsume = linesRemoved;
        int currentLevel = levelProperty.get();

        while (toConsume > 0) {
            if (toConsume >= remaining) {
                // we reach the next level
                toConsume -= remaining;

                currentLevel++;
                levelProperty.set(currentLevel);

                // requirement for the *next* level:
                // for level N -> N+1: 3 + 2*(N-1)
                int requiredForNext = 3 + 2 * (currentLevel - 1);
                remaining = requiredForNext;

                // optional visual/sound feedback in the GUI
                if (viewGuiController != null) {
                    viewGuiController.onLevelUp(currentLevel);
                }
            } else {
                // stay on the same level, just reduce remaining
                remaining -= toConsume;
                toConsume = 0;
            }
        }

        linesRemainingForNextLevel = remaining;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            clearRow = handleBrickLanded();
        } else if (event.getEventSource() == EventSource.USER) {
            board.getScore().add(1);
        }

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();

        // reset level & lines for a fresh run
        levelProperty.set(1);
        linesClearedProperty.set(0);
        linesRemainingForNextLevel = 3; // Level 1 -> 2 needs 3 lines

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

}
