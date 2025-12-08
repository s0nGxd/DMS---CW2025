package com.comp2042.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the player's score using JavaFX properties for binding.
 * Provides observable score updates for UI elements.
 */

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Gets the score property for binding to UI components.
     *
     * @return the IntegerProperty representing the current score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }
    /**
     * Adds points to the current score.
     * @param i the number of points to add
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }
    /**
     * Resets the score to zero.
     */
    public void reset() {
        score.setValue(0);
    }
}
