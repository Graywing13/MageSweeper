package ui.utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// this class formats/stylizes the JButtons I make when they are constructed
public class JButtonCustom extends JButton {
    public JButtonCustom(String buttonText, ButtonType buttonType, ActionListener l) {
        this.setText(buttonText);
        this.setBackground(buttonType.bkgColor);
        this.setForeground(buttonType.textColor);
        this.setFont(new Font("Courier New", Font.PLAIN, 18));
        this.addActionListener(l);
        this.setSize(buttonType.width, buttonType.height);
    }
}
