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
import com.comp2042.model.LevelManager;
import com.comp2042.model.HighScoreManager;

public class GameController implements InputEventListener {

    // 2 hidden rows, 20 visible = 22
    private static final int BOARD_ROWS = 22;
    private static final int BOARD_COLUMNS = 10;

    private final Board board = new SimpleBoard(BOARD_ROWS, BOARD_COLUMNS);

    private final GuiController viewGuiController;

    private final HighScoreManager highScoreManager;

    // encapsulates level progression + total lines cleared.
    private final LevelManager levelManager = new LevelManager();

    public GameController(GuiController c, HighScoreManager highScoreManager) {
        this.viewGuiController = c;
        this.highScoreManager = highScoreManager;

        // Let GUI know about this GameController + highScores
        viewGuiController.setGameController(this);

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());

        // bind HUD directly to the LevelManager properties
        viewGuiController.bindLevel(levelManager.levelProperty());
        viewGuiController.bindLines(levelManager.linesClearedProperty());
    }

    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }
    
    private ClearRow handleBrickLanded() {
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());

            boolean leveledUp =
                levelManager.applyLineClearProgression(clearRow.getLinesRemoved());
            if (leveledUp && viewGuiController != null) {
                // notify GUI so it can show popup, adjust drop speed, play SFX
                viewGuiController.onLevelUp(levelManager.getLevel());
            }
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

        // reset level & lines for a fresh run
        levelManager.reset();

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

}
