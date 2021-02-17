package ui.utilities;

import ui.ColorTheme;

import java.awt.*;

public enum LabelType {
    SELECTION_TITLE(24, ColorTheme.BLACK, ColorTheme.TRANSPARENT, Font.BOLD),
    SELECTION_NORMAL(18, ColorTheme.BLACK, ColorTheme.TRANSPARENT, Font.BOLD),
    SELECTION_ACTION(18, ColorTheme.ORANGE, ColorTheme.TRANSLUCENT_BLACK, Font.PLAIN),
    IN_GAME_NORMAL_WHITE(24, ColorTheme.WHITE, ColorTheme.TRANSPARENT, Font.BOLD),
    IN_GAME_BIG_BLACK(60, ColorTheme.BLACK, ColorTheme.TRANSPARENT, Font.BOLD);

    final int fontSize;
    final ColorTheme fontColor;
    final ColorTheme fontBackground;
    final int fontFormatting;

    LabelType(int fontSize, ColorTheme fontColor, ColorTheme fontBackground, int fontFormatting) {
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.fontBackground = fontBackground;
        this.fontFormatting = fontFormatting;
    }
}
