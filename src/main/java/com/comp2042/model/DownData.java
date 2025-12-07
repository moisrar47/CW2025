package com.comp2042.model;

/**
 * Immutable value object that bundles the result of a downward game tick.
 * <p>
 * It carries information about any cleared rows and the updated view data
 * after the active brick has moved (or landed).
 */
public final class DownData {

    /**
     * Details about cleared rows and score bonus, if any.
     */
    private final ClearRow clearRow;

    /**
     * Snapshot of the board state to be rendered after the down movement.
     */
    private final ViewData viewData;

    /**
     * Creates a new {@code DownData} result.
     *
     * @param clearRow information about lines cleared during this tick,
     *                 or {@code null} if no lines were cleared
     * @param viewData the updated view data after processing the down movement
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Returns information about any lines cleared during the down movement.
     *
     * @return the {@link ClearRow} result, or {@code null} if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Returns the updated view data after the down movement.
     *
     * @return the {@link ViewData} snapshot for rendering
     */
    public ViewData getViewData() {
        return viewData;
    }
}
