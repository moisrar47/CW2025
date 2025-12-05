package com.comp2042.view;

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

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int HIDDEN_TOP_ROWS = 2;  // top rows aren't shown to player
    private static final int DROP_INTERVAL_MS = 400;

    private static final Color GRID_COLOR = Color.rgb(40, 40, 40); // dark grey lines
    private static final Color EMPTY_CELL_COLOR = Color.BLACK;    // well background

    @FXML
    private StackPane rootPane;

    @FXML
    private GridPane gamePanel;

    @FXML
    private StackPane pauseOverlay;

    @FXML
    private Group groupNotification;

    @FXML
    private Pane brickPanel;

    @FXML
    private Pane ghostPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private BorderPane gameBoard;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private ViewData lastViewData;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private boolean ignoreNextMouseClick = false;

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
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
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

        brickPanel.toFront(); // this ensures the falling brick layer is drawn above the background grid

        // small fixed margin so the grid is not glued to the top left of the window
        gameBoard.setLayoutX(20);
        gameBoard.setLayoutY(20);

    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = HIDDEN_TOP_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                // uses board value (0 at start) so to get black fill + grid stroke
                setRectangleData(boardMatrix[i][j], rectangle);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - HIDDEN_TOP_ROWS);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setActiveBrickRectangleData(brick.getBrickData()[i][j], rectangle);
                rectangles[i][j] = rectangle;
                brickPanel.getChildren().add(rectangle);
            }
        }

        // ghost outline uses the same 4x4 structure
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setVisible(false); // start hidden
                ghostRectangles[i][j] = rectangle;
                ghostPanel.getChildren().add(rectangle);
            }
        }

        // positions the active brick after the layout pass to avoid the initial "flash" issue
        Platform.runLater(() -> refreshBrick(brick));

        // make the cyan BorderPane exactly wrap the visible grid
        gameBoard.applyCss(); // ensure insets are up to date

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
            Duration.millis(DROP_INTERVAL_MS),
            ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

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
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = HIDDEN_TOP_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setStroke(GRID_COLOR); // nice grid line
        rectangle.setStrokeWidth(0.5);  // thin
        rectangle.setArcHeight(0);   // no rounded corners
        rectangle.setArcWidth(0);
    }

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

    private void setGhostRectangleData(int color, Rectangle rectangle) {
        // this is only called when color != 0 but keeps styling explicit
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(getFillColor(color)); // same colour as piece, just outline
        rectangle.setStrokeWidth(0.8);
        rectangle.setArcHeight(0);
        rectangle.setArcWidth(0);
    }

    private void handleMouseMoved(MouseEvent mouseEvent) {
        if (isPause.get() || pauseOverlay.isVisible() || isGameOver.get()
            || eventListener == null || lastViewData == null) {
            return;
        }

        // Convert mouse position into gamePanel's local coordinates
        javafx.geometry.Point2D localPoint =
            gamePanel.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        double width = brickPanel.getWidth();
        if (width <= 0) {
            return;
        }

        // Mouse X within the brick panel
        double x = mouseEvent.getX();
        if (x < 0) x = 0;
        if (x > width) x = width;

        int columns = displayMatrix[0].length;
        double cellWidth = width / columns;

        int targetColumn = (int) (x / cellWidth);
        if (targetColumn < 0) targetColumn = 0;
        if (targetColumn >= columns) targetColumn = columns - 1;

        // Find the horizontal coverage of the current piece: [pieceLeft, pieceRight]
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
        } else if (targetColumn > pieceRight) {
            refreshBrick(eventListener.onRightEvent(
                new MoveEvent(EventType.RIGHT, EventSource.USER)
            ));
        }

        // if targetColumn is between pieceLeft and pieceRight, do nothing
        gamePanel.requestFocus();

    }

    private void handleMouseClicked(MouseEvent mouseEvent) {
        // if pause overlay is up, never treat this as a gameplay click
        if (isPause.get() || pauseOverlay.isVisible() || isGameOver.get()) {
            return;
        }

        // if we just closed pause with a button, ignore this first click
        if (ignoreNextMouseClick) {
            ignoreNextMouseClick = false;
            return;
        }

        // left click or middle click ---> hard drop
        if (mouseEvent.getButton() == MouseButton.PRIMARY ||
            mouseEvent.getButton() == MouseButton.MIDDLE) {

            hardDrop();
            mouseEvent.consume();
        }

        gamePanel.requestFocus();
    }

    private void handleScroll(ScrollEvent event) {
        if (isPause.get() || isGameOver.get() || eventListener == null) {
            return;
        }

        // Any scroll (up or down) rotates the piece once
        if (event.getDeltaY() != 0) {
            refreshBrick(eventListener.onRotateEvent(
                new MoveEvent(EventType.ROTATE, EventSource.USER)
            ));
            event.consume();
        }

        gamePanel.requestFocus();
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void hardDrop() {
        // Don’t do anything if paused or game over
        if (isPause.getValue() == Boolean.TRUE || isGameOver.getValue() == Boolean.TRUE) {
            return;
        }

        DownData downData;

        do {
            downData = eventListener.onDownEvent(
                new MoveEvent(EventType.DOWN, EventSource.USER)
            );
        } while (downData.getClearRow() == null);

        // If we actually cleared any lines, show the floating +score popup
        if (downData.getClearRow().getLinesRemoved() > 0) {
            NotificationPanel notificationPanel =
                new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }

        // refresh the active brick view (this will now be the newly spawned piece)
        refreshBrick(downData.getViewData());

        gamePanel.requestFocus();
    }

    private void togglePause() {
        // do not pause after game over or before timeline exists
        if (isGameOver.get() || timeLine == null) {
            return;
        }

        boolean pauseNow = !isPause.get();
        isPause.set(pauseNow);

        if (pauseNow) {
            timeLine.pause();
            pauseOverlay.setVisible(true);
        } else {
            pauseOverlay.setVisible(false);
            timeLine.play();
            gamePanel.requestFocus();
        }
    }

    @FXML
    private void handleResume(ActionEvent event) {
        if (isPause.get()) {
            ignoreNextMouseClick = true;
            togglePause();
        }
    }

    @FXML
    private void handleNewGameFromPause(ActionEvent event) {
        // clear pause state and start a fresh game
        ignoreNextMouseClick = true;
        isPause.set(false);
        pauseOverlay.setVisible(false);
        newGame(event);
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString());
    }

    public void gameOver() {
        timeLine.stop();
        pauseOverlay.setVisible(false);
        isPause.setValue(Boolean.FALSE);
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        pauseOverlay.setVisible(false);
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
