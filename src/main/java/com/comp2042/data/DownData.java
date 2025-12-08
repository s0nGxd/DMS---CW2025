package com.comp2042.data;

/**
 * Data container for the result of downward movement events.
 * Contains information about cleared rows and updated view state.
 */

public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a DownData with clear row and view information.
     * @param clearRow information about cleared rows, or null if none
     * @param viewData the updated view state after movement
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the clear row information.
     * @return ClearRow object containing cleared line data, or null if no lines cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated view state.
     * @return ViewData representing the current game state
     */
    public ViewData getViewData() {
        return viewData;
    }
}
