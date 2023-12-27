package com.game;

import java.awt.FontMetrics;

public class HelperFunctions {
    
    // Static variables
    static int someStaticVariable;

    // Static block
    static {
        // This block is executed when the class is loaded.
        someStaticVariable = 0;
    }

    public static int computeSpeedup(int timeElapsed) {
        return (int) (Math.log(timeElapsed/1000+2)/(Math.log(2)));
    }

    public static int getXForCenteringText(String titleString, FontMetrics fontMetrics) {
        int textWidth = fontMetrics.stringWidth(titleString);
        return (Integer.parseInt(SettingsManager.getSetting("game.window.width")) - textWidth) / 2; // Calculate X-coordinate for centering
    }
}
