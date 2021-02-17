package ui.utilities;

import ui.ColorTheme;

import java.awt.*;

// This class stores the format/style of the JButtons that I will be using.
public enum ButtonType {
    NORMAL(ColorTheme.BLACK.color, ColorTheme.WHITE.color, 300, 50),
    SELECTION_MENU_NEXT(ColorTheme.BLACK.color, ColorTheme.WHITE.color, 100, 50);

    final Color bkgColor;
    final Color textColor;
    final int width;
    final int height;

    // EFFECTS: constructor for buttons that sets their respective properties
    ButtonType(Color bkgColor, Color textColor, int width, int height) {
        this.bkgColor = bkgColor;
        this.textColor = textColor;
        this.height = height;
        this.width = width;
    }
}
