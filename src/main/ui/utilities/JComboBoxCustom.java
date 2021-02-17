package ui.utilities;

import javax.swing.*;
import java.awt.*;

// this class extends JComboBox and modifies its font formatting and aesthetics
public class JComboBoxCustom<E> extends JComboBox<E> {
    private static final int FONT_SIZE = 18;

    // EFFECTS: creates a new JComboBox that has the given formatting specifications
    @SafeVarargs
    public JComboBoxCustom(E... strings) {
        super(strings);
        this.setFont(new Font("Courier New", Font.BOLD, FONT_SIZE));
    }
}
