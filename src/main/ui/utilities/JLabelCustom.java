package ui.utilities;

import ui.ColorTheme;

import javax.swing.*;
import java.awt.*;

// this class is for making JLabels that are formatted the way I want them to be
public class JLabelCustom extends JLabel {

    // EFFECTS: Creates a new JLabel with certain properties
    public JLabelCustom(String textToDisplay, LabelType labelType) {
        this.setText(textToDisplay);
        this.setForeground(labelType.fontColor.color);
        if (!labelType.fontBackground.equals(ColorTheme.TRANSPARENT)) {
            this.setOpaque(true);
            this.setBackground(labelType.fontBackground.color);
        }
        this.setFont(new Font("Courier New", labelType.fontFormatting, labelType.fontSize));
        this.setSize(getPreferredSize());
    }
}
