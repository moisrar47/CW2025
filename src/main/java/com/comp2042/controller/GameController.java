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

public class GameController implements InputEventListener {

    // 2 hidden rows, 20 visible = 22
    private static final int BOARD_ROWS = 22;
    private static final int BOARD_COLUMNS = 10;

    private final Board board = new SimpleBoard(BOARD_ROWS, BOARD_COLUMNS);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    private ClearRow handleBrickLanded() {
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        return clearRow;
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
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
