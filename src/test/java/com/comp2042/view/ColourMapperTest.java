package com.comp2042.view;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColourMapperTest {

    private final ColourMapper mapper = new ColourMapper();

    @Test
    @DisplayName("Maps ID 0 to Transparent")
    void testTransparent() {
        assertEquals(Color.TRANSPARENT, mapper.getFillColor(0));
    }

    @Test
    @DisplayName("Maps ID 1 to Cyan (I-Piece)")
    void testIPieceColor() {
        // ID 1 usually Aqua/Cyan
        assertEquals(Color.AQUA, mapper.getFillColor(1));
    }

    @Test
    @DisplayName("Maps unknown ID to White")
    void testDefaultColor() {
        assertEquals(Color.WHITE, mapper.getFillColor(999));
    }
}