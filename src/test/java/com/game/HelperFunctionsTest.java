package com.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.FontMetrics;
import static org.mockito.Mockito.*;

class HelperFunctionsTest {

    @Test
    void computeSpeedup_ZeroElapsedTime() {
        // Given
        int timeElapsed = 0;

        // When
        double result = HelperFunctions.computeSpeedup(timeElapsed);

        // Then
        assertEquals(1, result, "Speedup should be 1 when time elapsed is 0");
    }

    @Test
    void computeSpeedup_NonZeroElapsedTime() {
        // Given
        int timeElapsed = 2000;

        // When
        double result = HelperFunctions.computeSpeedup(timeElapsed);

        // Then
        // We expect a certain value based on the speedup formula
        double expected = 2;
        assertEquals(expected, result, "Speedup calculation did not match expected value");
    }

    @Test
    void getXForCenteringText_EmptyString() {
        // Given
        String titleString = "";
        FontMetrics fontMetrics = createMockFontMetricsForString(titleString);

        // When
        int result = HelperFunctions.getXForCenteringText(titleString, fontMetrics);

        // Then
        int expected = Integer.parseInt(SettingsManager.getSetting("game.window.width")) / 2;
        assertEquals(expected, result, "X coordinate for centering an empty string should be half of window width");
    }

    // Additional test methods for other scenarios...

    private FontMetrics createMockFontMetricsForString(String text) {
        // You would need to mock the FontMetrics behavior here. For example, using Mockito:
        FontMetrics mockFontMetrics = mock(FontMetrics.class);
        when(mockFontMetrics.stringWidth(text)).thenReturn(text.length() * 6); // Assuming each character is 6 pixels wide
        return mockFontMetrics;
    }
}
