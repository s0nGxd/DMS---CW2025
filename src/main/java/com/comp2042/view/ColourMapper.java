package com.comp2042.view;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Maps integer brick type codes to JavaFX Paint colors.
 * Provides consistent coloring for all brick types throughout the game.
 */

public class ColourMapper {

    /**
     * Gets the fill color for a given brick type code.
     * @param i the brick type code (0-7)
     * @return Paint object representing the color for this brick type
     */
    public Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
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
}