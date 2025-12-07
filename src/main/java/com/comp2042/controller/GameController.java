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

/**
 * Central controller for the Tetris game.
 * <p>
 * Receives input events from the {@link EventSource}, updates the {@link Board} model,
 * coordinates level progression via {@link LevelManager}, notifies the
 * {@link GuiController} to refresh the view, and reports finished games to the
 * {@link HighScoreManager}.
 */
public class GameController implements InputEventListener {

    // 2 hidden rows, 20 visible = 22
    private static final int BOARD_ROWS = 22;
    private static final int BOARD_COLUMNS = 10;

    private final Board board = new SimpleBoard(BOARD_ROWS, BOARD_COLUMNS);

    private final GuiController viewGuiController;

    private final HighScoreManager highScoreManager;

    // encapsulates level progression + total lines cleared
    private final LevelManager levelManager = new LevelManager();

    /**
     * Creates a new {@code GameController} bound to the given GUI controller
     * and high-score manager.
     * <p>
     * Registers this controller with the GUI, creates the first brick on the board,
     * sets up the initial game view, and binds the score and HUD labels directly
     * to the underlying model and {@link com.comp2042.model.LevelManager} properties.
     *
     * @param c                 the GUI controller used to render the game
     * @param highScoreManager  the manager responsible for loading and saving high scores
     */
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

    /**
     * Returns the high-score manager used by this controller.
     * <p>
     * The GUI uses this to display and update the persistent high-score list
     * when a game finishes.
     *
     * @return the {@link HighScoreManager} associated with this game
     */
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }

    /**
     * Handles all logic that occurs when the active brick can no longer move down
     * and has landed on the board.
     * <p>
     * Merges the brick into the background, clears any full rows, updates the score
     * and level progression, notifies the GUI about level-ups and game over, and
     * refreshes the background grid.
     *
     * @return a {@link com.comp2042.model.ClearRow} describing how many lines were
     *         removed and the score bonus awarded for this landing
     */
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

    /**
     * Handles a downward movement event for the active brick.
     * <p>
     * If the brick can move down, it is advanced one row and a small score
     * bonus is applied for user-initiated soft drops. If it can no longer
     * move, the brick is merged into the background and any full rows are
     * cleared. The result is returned as a {@link DownData} snapshot.
     *
     * @param event the move event that triggered the down action
     * @return a {@link DownData} object containing information about cleared rows
     *         (if any) and the updated view state
     */
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

    /**
     * Handles a left-move input event for the active brick.
     * <p>
     * Attempts to move the brick one column to the left and then returns the
     * updated {@link ViewData} for rendering.
     *
     * @param event the input event that triggered the left movement
     * @return the updated view data after applying the move
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles a right-move input event for the active brick.
     * <p>
     * Attempts to move the brick one column to the right and then returns the
     * updated {@link ViewData} for rendering.
     *
     * @param event the input event that triggered the right movement
     * @return the updated view data after applying the move
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles a rotate input event for the active brick.
     * <p>
     * Rotates the active brick (if the rotation is valid within the board
     * boundaries) and returns the updated {@link ViewData} so the GUI can
     * redraw the new orientation.
     *
     * @param event the input event that triggered the rotation
     * @return the updated view data after applying the rotation
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Starts a new game session.
     * <p>
     * Clears the board, resets level and line counters via the {@link LevelManager},
     * and refreshes the background grid in the GUI so the player sees a fresh board.
     */
    @Override
    public void createNewGame() {
        board.newGame();
        levelManager.reset();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

}
