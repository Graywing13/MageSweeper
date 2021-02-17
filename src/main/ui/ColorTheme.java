package ui;

import java.awt.*;

// A class that provides a single point of control for the various colours used in the swing application
public enum ColorTheme {
    ORANGE(new Color(255, 125, 51)),
    BLUE(new Color(83, 156, 157)),
    TRANSPARENT(new Color(0, 0, 0, 0)),
    TRANSLUCENT_BLACK(new Color(0, 0, 0, (float) 0.5)),
    TRANSLUCENT_DARK_BLACK(new Color(0, 0, 0, (float) 0.8)),
    TRANSLUCENT_RED(new Color(255, 0, 0, 130)),
    TRANSLUCENT_BLUE(new Color(28, 44, 233, 133)),
    BLACK(Color.black),
    WHITE(Color.white);

    public final Color color;

    // EFFECTS: constructor that sets this' color to the specified value
    ColorTheme(Color color) {
        this.color = color;
    }
}
