package com.comp2042.view;

import com.comp2042.model.HighScoreEntry;
import com.comp2042.controller.GameController;
import com.comp2042.model.HighScoreManager;
import com.comp2042.audio.AudioManager;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import com.comp2042.input.InputEventListener;
import com.comp2042.model.ViewData;
import com.comp2042.input.MoveEvent;
import com.comp2042.input.EventType;
import com.comp2042.input.EventSource;
import com.comp2042.model.DownData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.geometry.Bounds;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

/**
 * JavaFX controller for the main Tetris game UI.
 * <p>
 * Handles keyboard and mouse input, HUD updates, pause and game over overlays,
 * ghost piece and next-piece rendering, drop timing, audio integration, and
 * high-score interactions.
 */
public class GuiController implements Initializable {

    /** Pixel size of each visible cell in the main playfield. */
    private static final int BRICK_SIZE = 26;

    /** Pixel size used for cells in the next-piece preview panel. */
    private static final int NEXT_BRICK_SIZE = BRICK_SIZE - 8;

    /** Number of top rows that are hidden from the player in the visible grid. */
    private static final int HIDDEN_TOP_ROWS = 2;  // top rows aren't shown to player

    /** Base drop interval (in milliseconds) for the falling bricks at level 1. */
    private static final int DROP_INTERVAL_MS = 800; // decent brick speed

    private int currentDropIntervalMs = DROP_INTERVAL_MS;

    private static final Color GRID_COLOR = Color.rgb(40, 40, 40); // dark grey lines
    private static final Color EMPTY_CELL_COLOR = Color.BLACK;    // well background

    @FXML
    private StackPane rootPane;

    @FXML
    private GridPane gamePanel;

    @FXML
    private GridPane nextPiecePanel;

    @FXML
    private StackPane pauseOverlay;

    @FXML
    private StackPane gameOverOverlay;

    @FXML
    private Button musicToggleButton;

    @FXML
    private Group groupNotification;

    @FXML
    private Pane brickPanel;

    @FXML
    private Pane ghostPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label linesLabel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private VBox highScoreListBox;

    @FXML
    private Label highScoreLine1;

    @FXML
    private Label highScoreLine2;

    @FXML
    private Label highScoreLine3;

    @FXML
    private BorderPane gameBoard;

    @FXML
    private Slider volumeSlider;

    @FXML
    private StackPane highScoreOverlay;

    @FXML
    private Label highScoreScoreLabel;

    @FXML
    private TextField highScoreNameField;

    private HighScoreManager highScoreManager;
    private int lastFinalScore = 0;


    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] nextPieceRectangles;

    private ViewData lastViewData;

    private Timeline timeLine;

    // Audio (basically delegated to AudioManager to reduce GuiController responsibilities)
    private final AudioManager audioManager = new AudioManager();

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private boolean ignoreNextMouseClick = false;

    /**
     * Called automatically by the JavaFX runtime after FXML loading.
     * <p>
     * Sets up fonts, key and mouse handlers, pause and game over overlays,
     * audio controls, and initial background music state.
     *
     * @param location  location used to resolve relative paths, or {@code null}
     * @param resources the resources used to localise the root object, or {@code null}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                // toggle pause with P or Esc
                if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                    keyEvent.consume();
                    return;
                }

                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        audioManager.playMoveSound();
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        audioManager.playMoveSound();
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        audioManager.playRotateSound();
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        audioManager.playMoveSound();
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }

                if (keyEvent.getCode() == KeyCode.SPACE) {
                    hardDrop();
                    keyEvent.consume();
                }
            }
        });

        rootPane.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        rootPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClicked);
        rootPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);

        pauseOverlay.setVisible(false);

        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

        // anchor the grid to the bottom of the cyan BorderPane
        BorderPane.setAlignment(gamePanel, Pos.BOTTOM_CENTER);

        brickPanel.toFront(); // this ensures the falling bricklayer is drawn above the background grid

        /* this will shift notifications left so they are centered over the main playfield,
         not over the whole window (which now includes the NEXT panel) */
        groupNotification.setTranslateX(-60); // this value is eye-balled

        updateMusicToggleText();

        // start music from the get go (only if not paused / game over)
        Platform.runLater(() ->
            audioManager.startBackgroundMusic(isPause.get(), isGameOver.get())
        );

        if (volumeSlider != null) {
            volumeSlider.setValue(audioManager.getMasterVolume());
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                audioManager.setMasterVolume(newVal.doubleValue());
            });

            // when user lets go of the slider, return focus to the game panel
            volumeSlider.setOnMouseReleased(e -> gamePanel.requestFocus());
        }

    }

    /**
     * Initialises the visible game view based on the given board matrix and
     * initial brick view data.
     * <p>
     * Creates the background grid, active brick and ghost layers, next-piece
     * preview, resizes the surrounding frame, and starts the drop timeline.
     *
     * @param boardMatrix the board matrix representing settled blocks
     * @param brick       snapshot of the current brick and next-piece data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // main board grid
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int row = HIDDEN_TOP_ROWS; row < boardMatrix.length; row++) {
            for (int col = 0; col < boardMatrix[row].length; col++) {
                Rectangle cell = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(boardMatrix[row][col], cell);
                displayMatrix[row][col] = cell;
                gamePanel.add(cell, col, row - HIDDEN_TOP_ROWS);
            }
        }

        // active brick 4x4 matrix
        int brickRows = brick.getBrickData().length;
        int brickCols = brick.getBrickData()[0].length;

        rectangles = new Rectangle[brickRows][brickCols];
        for (int r = 0; r < brickRows; r++) {
            for (int c = 0; c < brickCols; c++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setActiveBrickRectangleData(brick.getBrickData()[r][c], rect);
                rectangles[r][c] = rect;
                brickPanel.getChildren().add(rect);
            }
        }

        // ghost brick 4x4 matrix
        ghostRectangles = new Rectangle[brickRows][brickCols];
        for (int r = 0; r < brickRows; r++) {
            for (int c = 0; c < brickCols; c++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setVisible(false);
                ghostRectangles[r][c] = rect;
                ghostPanel.getChildren().add(rect);
            }
        }

        // next piece preview grid (created once)
        if (nextPiecePanel != null) {
            int[][] nextData = brick.getNextBrickData();
            if (nextData != null) {
                int rows = nextData.length;
                int cols = nextData[0].length;

                nextPieceRectangles = new Rectangle[rows][cols];
                nextPiecePanel.getChildren().clear();

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        Rectangle rect = new Rectangle(NEXT_BRICK_SIZE, NEXT_BRICK_SIZE);
                        nextPieceRectangles[r][c] = rect;
                        nextPiecePanel.add(rect, c, r);
                    }
                }

                // initial fill / visibility
                refreshNextPiece(brick);
            }
        }

        // position active brick (and ghost + preview) after layout
        Platform.runLater(() -> refreshBrick(brick));

        // make the cyan BorderPane exactly wrap the visible grid
        gameBoard.applyCss();

        int columns = boardMatrix[0].length;
        int visibleRows = boardMatrix.length - HIDDEN_TOP_ROWS;

        double hgap = gamePanel.getHgap();
        double vgap = gamePanel.getVgap();

        double gridWidth = columns * BRICK_SIZE + (columns - 1) * hgap;
        double gridHeight = visibleRows * BRICK_SIZE + (visibleRows - 1) * vgap;

        double borderWidth = gameBoard.getInsets().getLeft() + gameBoard.getInsets().getRight();
        double borderHeight = gameBoard.getInsets().getTop() + gameBoard.getInsets().getBottom();

        double frameWidth = gridWidth + borderWidth;
        double frameHeight = gridHeight + borderHeight;

        gameBoard.setPrefWidth(frameWidth);
        gameBoard.setPrefHeight(frameHeight);
        gameBoard.setMinWidth(frameWidth);
        gameBoard.setMinHeight(frameHeight);
        gameBoard.setMaxWidth(frameWidth);
        gameBoard.setMaxHeight(frameHeight);

        timeLine = new Timeline(new KeyFrame(
            Duration.millis(currentDropIntervalMs),
            ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Maps a numeric cell value to a corresponding {@link Color}.
     *
     * @param i the cell value
     * @return a {@link Paint} used to fill that cell
     */
    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = EMPTY_CELL_COLOR;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }

    /**
     * Updates the active brick, ghost piece, and next-piece preview based on
     * the provided {@link ViewData}, unless the game is currently paused.
     *
     * @param brick view data describing the current brick state
     */
    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            lastViewData = brick;
            int[][] data = brick.getBrickData();
            int baseX = brick.getxPosition();
            int baseY = brick.getyPosition();
            int ghostBaseY = brick.getGhostYPosition();

            // 1 Active falling piece
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    Rectangle rectangle = rectangles[i][j];
                    int color = data[i][j];

                    if (color == 0) {
                        // hide empty cells of the 4x4 matrix
                        setActiveBrickRectangleData(0, rectangle);
                        rectangle.setVisible(false);
                        continue;
                    }

                    int boardX = baseX + j;
                    int boardY = baseY + i;

                    // skip parts of the brick that are still in hidden rows or out of bounds
                    if (boardY < HIDDEN_TOP_ROWS ||
                        boardY >= displayMatrix.length ||
                        boardX < 0 ||
                        boardX >= displayMatrix[0].length) {
                        rectangle.setVisible(false);
                        continue;
                    }

                    // look up the background cell this block is over
                    Rectangle cell = displayMatrix[boardY][boardX];
                    Bounds cellBounds = cell.getBoundsInParent();

                    rectangle.setVisible(true);
                    rectangle.setTranslateX(cellBounds.getMinX());
                    rectangle.setTranslateY(cellBounds.getMinY());
                    setActiveBrickRectangleData(color, rectangle);
                }
            }

            // 2 Ghost landing outline
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    Rectangle ghostRect = ghostRectangles[i][j];
                    int color = data[i][j];

                    if (color == 0) {
                        ghostRect.setVisible(false);
                        continue;
                    }

                    int boardX = baseX + j;
                    int boardY = ghostBaseY + i;

                    if (boardY < HIDDEN_TOP_ROWS ||
                        boardY >= displayMatrix.length ||
                        boardX < 0 ||
                        boardX >= displayMatrix[0].length) {
                        ghostRect.setVisible(false);
                        continue;
                    }

                    Rectangle cell = displayMatrix[boardY][boardX];
                    Bounds cellBounds = cell.getBoundsInParent();

                    ghostRect.setVisible(true);
                    ghostRect.setTranslateX(cellBounds.getMinX());
                    ghostRect.setTranslateY(cellBounds.getMinY());
                    setGhostRectangleData(color, ghostRect);
                }
            }

            // 3) update the next piece preview window
            refreshNextPiece(brick);
        }
    }

    /**
     * Updates the next-piece preview panel to match the next brick contained
     * in the given {@link ViewData}.
     *
     * @param brick view data containing the next piece matrix
     */
    private void refreshNextPiece(ViewData brick) {
        if (nextPiecePanel == null || nextPieceRectangles == null) {
            return;
        }

        int[][] nextData = brick.getNextBrickData();
        if (nextData == null) {
            return;
        }

        int rows = nextData.length;
        int cols = nextData[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Rectangle rect = nextPieceRectangles[r][c];
                int color = nextData[r][c];

                if (color == 0) {
                    rect.setVisible(false);
                    setActiveBrickRectangleData(0, rect);
                } else {
                    rect.setVisible(true);
                    setActiveBrickRectangleData(color, rect);
                }
            }
        }
    }

    /**
     * Redraws the background grid using the given board matrix.
     *
     * @param board the current board state to render
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = HIDDEN_TOP_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Styles a background grid cell rectangle according to the given color code.
     *
     * @param color     numeric cell value
     * @param rectangle the {@link Rectangle} to style
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setStroke(GRID_COLOR); // nice grid line
        rectangle.setStrokeWidth(0.5);  // thin
        rectangle.setArcHeight(0);   // no rounded corners
        rectangle.setArcWidth(0);
    }

    /**
     * Styles a rectangle representing a cell of the active falling brick.
     * Empty cells are made transparent.
     *
     * @param color     numeric cell value
     * @param rectangle the {@link Rectangle} to style
     */
    private void setActiveBrickRectangleData(int color, Rectangle rectangle) {
        if (color == 0) {
            // don't show anything for empty cells in the 4x4 brick matrix
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(null);
        } else {
            rectangle.setFill(getFillColor(color));
            rectangle.setStroke(GRID_COLOR);
            rectangle.setStrokeWidth(0.5);
        }
        rectangle.setArcHeight(0);
        rectangle.setArcWidth(0);
    }

    /**
     * Styles a rectangle used for the ghost-piece outline.
     *
     * @param color     numeric cell value
     * @param rectangle the {@link Rectangle} to style
     */
    private void setGhostRectangleData(int color, Rectangle rectangle) {
        // this is only called when color != 0 but keeps styling explicit
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(getFillColor(color)); // same colour as piece, just outline
        rectangle.setStrokeWidth(0.8);
        rectangle.setArcHeight(0);
        rectangle.setArcWidth(0);
    }

    /**
     * Handles UI and audio feedback when lines are cleared.
     * <p>
     * Shows a floating "+score" notification and plays the line-clear sound
     * if any lines were actually removed.
     *
     * @param downData result of the last downward move, including clear-row info
     */
    private void handleLineClear(DownData downData) {
        if (downData == null ||
            downData.getClearRow() == null ||
            downData.getClearRow().getLinesRemoved() <= 0) {
            return;
        }

        // floating +score popup
        NotificationPanel notificationPanel =
            new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
        groupNotification.getChildren().add(notificationPanel);

        // score popup stays roughly in the centre
        notificationPanel.setTranslateX(-25);
        notificationPanel.setTranslateY(10);

        notificationPanel.showScore(groupNotification.getChildren());

        // line-clear SFX
        audioManager.playLineClearSound();
    }

    /**
     * Updates the music toggle button text to reflect the current music state.
     */
    private void updateMusicToggleText() {
        if (musicToggleButton != null) {
            musicToggleButton.setText(
                audioManager.isMusicEnabled() ? "Music: ON" : "Music: OFF"
            );
        }
    }

    /**
     * Handles horizontal mouse movement over the playfield by moving the
     * active brick left or right, if appropriate and if the game is active.
     *
     * @param mouseEvent the mouse movement event
     */
    private void handleMouseMoved(MouseEvent mouseEvent) {
        if (isPause.get() || pauseOverlay.isVisible() || isGameOver.get()
            || eventListener == null || lastViewData == null) {
            return;
        }

        // Convert mouse position into gamePanel's local coordinates
        javafx.geometry.Point2D local =
            gamePanel.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        double width  = gamePanel.getWidth();
        double height = gamePanel.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        double x = local.getX();
        double y = local.getY();

        // If the mouse is outside the visible well, don't move the piece
        if (x < 0 || x > width || y < 0 || y > height) {
            return;
        }

        int columns = displayMatrix[0].length;
        double cellWidth = width / columns;

        int targetColumn = (int) (x / cellWidth);
        if (targetColumn < 0) targetColumn = 0;
        if (targetColumn >= columns) targetColumn = columns - 1;

        // finds the horizontal coverage of the current piece: [pieceLeft, pieceRight]
        int[][] data = lastViewData.getBrickData();
        int baseX = lastViewData.getxPosition();

        int minRelX = Integer.MAX_VALUE;
        int maxRelX = Integer.MIN_VALUE;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] != 0) {
                    if (j < minRelX) minRelX = j;
                    if (j > maxRelX) maxRelX = j;
                }
            }
        }

        if (minRelX == Integer.MAX_VALUE) {
            // no visible blocks in this shape (shouldn't happen)
            return;
        }

        int pieceLeft = baseX + minRelX;
        int pieceRight = baseX + maxRelX;

        // Only move if the mouse column is completely outside the piece
        if (targetColumn < pieceLeft) {
            refreshBrick(eventListener.onLeftEvent(
                new MoveEvent(EventType.LEFT, EventSource.USER)
            ));
            audioManager.playMoveSound();
        } else if (targetColumn > pieceRight) {
            refreshBrick(eventListener.onRightEvent(
                new MoveEvent(EventType.RIGHT, EventSource.USER)
            ));
            audioManager.playMoveSound();
        }

        // if targetColumn is between pieceLeft and pieceRight, do nothing
        gamePanel.requestFocus();

    }

    /**
     * Handles mouse clicks for gameplay, using left or middle clicks to
     * trigger a hard drop when no overlay is active.
     *
     * @param mouseEvent the mouse click event
     */
    private void handleMouseClicked(MouseEvent mouseEvent) {
        // 1) Swallow the very next click after closing a menu
        if (ignoreNextMouseClick) {
            ignoreNextMouseClick = false;
            return;
        }

        // 2) If a menu overlay is open, ignore this click for gameplay
        if ((pauseOverlay != null && pauseOverlay.isVisible()) ||
            (gameOverOverlay != null && gameOverOverlay.isVisible())) {
            return;
        }

        if (isPause.get() || isGameOver.get()) {
            return;
        }

        // Left click or middle click -> hard drop
        if (mouseEvent.getButton() == MouseButton.PRIMARY ||
            mouseEvent.getButton() == MouseButton.MIDDLE) {

            hardDrop();
            mouseEvent.consume();
        }

        gamePanel.requestFocus();
    }

    /**
     * Handles scroll wheel input by rotating the active piece, if the
     * game is not paused or over.
     *
     * @param event the scroll event
     */
    private void handleScroll(ScrollEvent event) {
        if (isPause.get() || isGameOver.get() || eventListener == null) {
            return;
        }

        // Any scroll (up or down) rotates the piece once
        if (event.getDeltaY() != 0) {
            refreshBrick(eventListener.onRotateEvent(
                new MoveEvent(EventType.ROTATE, EventSource.USER)
            ));
            audioManager.playRotateSound();
            event.consume();
        }

        gamePanel.requestFocus();
    }

    /**
     * Moves the active brick down by one step, updates the view, plays
     * appropriate sounds, and processes any resulting line clears.
     *
     * @param event the move event indicating the source of the drop
     */
    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);

            // only plays soft drop sound for user initiated drops, not timer thread
            if (event.getEventSource() == EventSource.USER) {
                audioManager.playSoftDropSound();
            }

            // handle line clear popup + SFX (uses DownData, not ClearRow)
            handleLineClear(downData);

            // update active brick view
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Performs a hard drop of the active brick, repeatedly moving it down
     * until it locks, then playing the hard-drop sound, handling any line
     * clears, and refreshing the view.
     */
    private void hardDrop() {
        // don’t do anything if paused or game over
        if (isPause.getValue() == Boolean.TRUE || isGameOver.getValue() == Boolean.TRUE) {
            return;
        }

        DownData downData;

        // keep dropping until the brick locks / a new piece is spawned
        do {
            downData = eventListener.onDownEvent(
                new MoveEvent(EventType.DOWN, EventSource.USER)
            );
        } while (downData.getClearRow() == null);

        // now the piece has actually landed -> play hard drop SFX once
        audioManager.playHardDropSound();

        // handle line clear popup + line clear SFX (shared with soft drops)
        handleLineClear(downData);

        // refresh the active brick view (this will now be the newly spawned piece)
        refreshBrick(downData.getViewData());

        gamePanel.requestFocus();
    }

    /**
     * Toggles the pause state of the game.
     * <p>
     * Pauses or resumes the drop timeline, shows or hides the pause overlay,
     * and pauses or resumes background music accordingly.
     */
    private void togglePause() {
        // do not pause after game over or before timeline exists
        if (isGameOver.get() || timeLine == null) {
            return;
        }

        boolean pauseNow = !isPause.get();
        isPause.set(pauseNow);

        if (pauseNow) {
            // going INTO pause
            timeLine.pause();
            pauseOverlay.setVisible(true);

            // pause music at current position (no restart)
            audioManager.pauseBackgroundMusic();
        } else {
            // coming OUT of pause
            pauseOverlay.setVisible(false);
            timeLine.play();

            // resume from current position if music is enabled and not game over
            audioManager.resumeBackgroundMusic(isGameOver.get());

            gamePanel.requestFocus();
        }
    }

    /**
     * Handles the pause overlay "Resume" button, unpausing the game if
     * currently paused and safely ignoring the next click.
     *
     * @param event the action event
     */
    @FXML
    private void handleResume(ActionEvent event) {
        if (isPause.get()) {
            ignoreNextMouseClick = true;
            togglePause();
        }
    }

    /**
     * Handles the pause overlay "New Game" button by clearing pause state
     * and starting a fresh game.
     *
     * @param event the action event
     */
    @FXML
    private void handleNewGameFromPause(ActionEvent event) {
        // clear pause state and start a fresh game
        ignoreNextMouseClick = true;
        isPause.set(false);
        pauseOverlay.setVisible(false);
        newGame(event);
    }

    /**
     * Toggles sound effects on or off and updates the SFX button label.
     *
     * @param event the action event
     */
    @FXML
    private void handleToggleSfx(ActionEvent event) {
        audioManager.toggleSfxEnabled();

        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            btn.setText(audioManager.isSfxEnabled() ? "SFX: On" : "SFX: Off");
        }
        // give keyboard focus back to the game panel
        gamePanel.requestFocus();
    }

    /**
     * Toggles background music on or off and updates the music button label.
     *
     * @param event the action event
     */
    @FXML
    private void handleToggleMusic(ActionEvent event) {
        audioManager.toggleMusicEnabled();

        if (event.getSource() instanceof Button) {
            Button btn = (Button) event.getSource();
            btn.setText(audioManager.isMusicEnabled() ? "Music: On" : "Music: Off");
        }

        // give keyboard focus back to the game panel
        gamePanel.requestFocus();
    }

    /**
     * Handles the exit button by stopping background music and closing
     * the application.
     *
     * @param event the action event
     */
    @FXML
    private void handleExit(ActionEvent event) {
        audioManager.stopBackgroundMusic();
        Platform.exit();
    }

    /**
     * Handles the game over "Play Again" button by starting a new game.
     *
     * @param event the action event
     */
    @FXML
    private void handleGameOverPlayAgain(ActionEvent event) {
        ignoreNextMouseClick = true;
        newGame(event);
    }

    /**
     * Saves a new high score using the entered player name, if valid,
     * then hides the high-score overlay and shows the game over overlay.
     *
     * @param event the action event
     */
    @FXML
    private void handleHighScoreSave(javafx.event.ActionEvent event) {
        if (highScoreOverlay != null) {
            String name = highScoreNameField != null
                ? highScoreNameField.getText().trim()
                : "";
            if (!name.isEmpty() && highScoreManager != null) {
                highScoreManager.recordHighScore(name, lastFinalScore);
            }
            highScoreOverlay.setVisible(false);
        }
        showGameOverOverlay();
    }

    /**
     * Skips saving a high score and proceeds directly to the game over overlay.
     *
     * @param event the action event
     */
    @FXML
    private void handleHighScoreSkip(javafx.event.ActionEvent event) {
        if (highScoreOverlay != null) {
            highScoreOverlay.setVisible(false);
        }
        showGameOverOverlay();
    }

    /**
     * Sets the input event listener used to communicate user input
     * back to the game controller and model.
     *
     * @param eventListener the listener to register
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score label to the given score property.
     *
     * @param integerProperty the score property from the model
     */
    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    /**
     * Binds the level label (if present) to the given level property.
     *
     * @param levelProperty the level property from the model
     */
    public void bindLevel(IntegerProperty levelProperty) {
        if (levelLabel != null) {
            levelLabel.textProperty().bind(levelProperty.asString());
        }
    }

    /**
     * Binds the lines-cleared label (if present) to the given property.
     *
     * @param linesProperty the lines-cleared property from the model
     */
    public void bindLines(IntegerProperty linesProperty) {
        if (linesLabel != null) {
            linesLabel.textProperty().bind(linesProperty.asString());
        }
    }

    /**
     * Handles visual and audio feedback when the player levels up.
     * <p>
     * Shows a "LEVEL X" notification, increases drop speed, and plays
     * a level-up sound.
     *
     * @param newLevel the new level reached
     */
    public void onLevelUp(int newLevel) {
        NotificationPanel notificationPanel =
            new NotificationPanel("LEVEL " + newLevel);
        groupNotification.getChildren().add(notificationPanel);

        // put LEVEL text slightly above the score popup
        notificationPanel.setTranslateY(-70); // moves it up;

        notificationPanel.showScore(groupNotification.getChildren());

        // make bricks fall faster for this level
        updateDropSpeedForLevel(newLevel);

        // play a short "level up" jingle
        audioManager.playLevelUpSound();
    }

    /**
     * Populates and shows the game over overlay, including a mini high-score
     * table if entries are available.
     */
    private void showGameOverOverlay() {
        // update the mini high-score table
        if (highScoreListBox != null && highScoreManager != null) {
            List<HighScoreEntry> entries = highScoreManager.getEntries();

            // fill up to 3 lines
            setHighScoreLine(highScoreLine1, 1, entries, 0);
            setHighScoreLine(highScoreLine2, 2, entries, 1);
            setHighScoreLine(highScoreLine3, 3, entries, 2);

            highScoreListBox.setVisible(!entries.isEmpty());
        }

        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(true);
            gameOverOverlay.toFront();
        }
    }

    /**
     * Updates a single high-score label to show the entry at the given index,
     * or hides it if no entry exists.
     *
     * @param label   label to update
     * @param rank    human-readable rank (1-based)
     * @param entries list of high-score entries
     * @param index   zero-based index into the entries list
     */
    private void setHighScoreLine(Label label, int rank,
                                  List<HighScoreEntry> entries, int index) {
        if (label == null) {
            return;
        }

        if (entries != null && index < entries.size()) {
            HighScoreEntry e = entries.get(index);
            label.setText(rank + ". " + e.getPlayerName() + " - " + e.getScore());
            label.setVisible(true);
        } else {
            label.setText("");
            label.setVisible(false);
        }
    }

    /**
     * Shows the high-score entry overlay pre-filled with the given final score.
     *
     * @param score the final score achieved in the last game
     */
    private void showHighScoreOverlay(int score) {
        lastFinalScore = score;

        if (highScoreScoreLabel != null) {
            highScoreScoreLabel.setText("Score: " + score);
        }
        if (highScoreNameField != null) {
            highScoreNameField.clear();
        }
        if (highScoreOverlay != null) {
            highScoreOverlay.setVisible(true);
            highScoreOverlay.toFront();
        }
    }

    /**
     * Handles the transition into the game over state.
     * <p>
     * Stops the drop timeline, stops music, plays the game-over sound,
     * parses the final score from the HUD, and shows either the high-score
     * entry overlay or the standard game over overlay.
     */
    public void gameOver() {
        if (timeLine != null) {
            timeLine.stop();
        }
        isGameOver.setValue(Boolean.TRUE);

        // stop music and play game over jingle
        audioManager.stopBackgroundMusic();
        audioManager.playGameOverSound();

        // hide pause overlay if somehow open
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
        }

        // determine final score from the HUD label
        int finalScore = 0;
        if (scoreLabel != null) {
            try {
                finalScore = Integer.parseInt(scoreLabel.getText());
            } catch (NumberFormatException ignored) {
                finalScore = 0;
            }
        }

        // decide which overlay to show
        if (highScoreManager != null && highScoreManager.isNewHighScore(finalScore)) {
            showHighScoreOverlay(finalScore);
        } else {
            showGameOverOverlay();
        }
    }

    /**
     * Injects the {@link GameController} and retrieves its high-score manager
     * for use by the GUI.
     *
     * @param controller the game controller instance
     */
    public void setGameController(GameController controller) {
        this.highScoreManager = controller.getHighScoreManager();
    }

    /**
     * Resets the UI for a new game: stops and resets the drop timeline,
     * hides overlays, clears active and ghost bricks, resets game state
     * via the event listener, and restarts background music.
     *
     * @param actionEvent the action event that triggered the new game, if any
     */
    public void newGame(ActionEvent actionEvent) {
        // stop the drop timer if it exists and reset speed to level 1
        if (timeLine != null) {
            timeLine.stop();
            timeLine.setRate(1.0);
        }

        // hide current active brick so the last piece from the previous
        // game doesn't flash briefly
        if (rectangles != null) {
            for (Rectangle[] row : rectangles) {
                for (Rectangle r : row) {
                    if (r != null) {
                        r.setVisible(false);
                    }
                }
            }
        }

        // hide ghost piece as well
        if (ghostRectangles != null) {
            for (Rectangle[] row : ghostRectangles) {
                for (Rectangle r : row) {
                    if (r != null) {
                        r.setVisible(false);
                    }
                }
            }
        }

        // avoid mouse logic trying to move an old piece between games
        lastViewData = null;

        // hide any overlays
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }
        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(false);
        }
        if (highScoreOverlay != null) {
            highScoreOverlay.setVisible(false);
        }

        isGameOver.set(false);
        isPause.set(false);

        // reset background music via AudioManager
        audioManager.stopBackgroundMusic();

        /* let the controller/model reset the board, score, level, lines
         and spawn a fresh piece */
        eventListener.createNewGame();

        gamePanel.requestFocus();

        // restarts the drop timeline from the beginning
        if (timeLine != null) {
            timeLine.playFromStart();
        }

        // resume background music if enabled
        audioManager.startBackgroundMusic(isPause.get(), isGameOver.get());
    }

    /**
     * Adjusts the drop timeline speed based on the current level, increasing
     * the rate by 25% per level up to a safety cap.
     *
     * @param level the current level
     */
    private void updateDropSpeedForLevel(int level) {
        if (timeLine == null) {
            return;
        }

        double rate = 1.0 + 0.25 * (level - 1); // 25% faster per level
        if (rate > 3.0) {
            rate = 3.0; // safety cap
        }

        timeLine.setRate(rate);
    }

}
