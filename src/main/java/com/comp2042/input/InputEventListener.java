package com.comp2042.input;

import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;

/**
 * Listener interface for receiving input events that affect the game state.
 * <p>
 * Implementations of this interface (such as the game controller) respond to
 * player actions or thread-generated movement events by updating the board
 * model and returning the corresponding updated view data.
 */
public interface InputEventListener {

    /**
     * Handles a downward movement event for the active brick.
     *
     * @param event the movement event that triggered the downward action
     * @return a {@link DownData} object describing any cleared rows and the
     *         updated view state after applying the movement
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles a left-move input event for the active brick.
     *
     * @param event the movement event that initiated the left shift
     * @return a {@link ViewData} snapshot representing the updated board view
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles a right-move input event for the active brick.
     *
     * @param event the movement event that initiated the right shift
     * @return a {@link ViewData} snapshot representing the updated board view
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles a rotation input event for the active brick.
     *
     * @param event the movement event that triggered the rotation
     * @return a {@link ViewData} snapshot representing the rotated brick state
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Requests that the implementing class reset the game state and begin a new game.
     */
    void createNewGame();
}
