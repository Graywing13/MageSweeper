package ui.tools;

import javax.swing.*;

// a class in charge of updating and displaying JPanels on the current frame; only one can exist at a time
//    to prevent multiple sources trying to change the frame.
public class FrameContentController {
    private static FrameContentController thisInstance = null;
    private JFrame frame;

    // EFFECTS: constructor for this class; sets this' JFrame
    private FrameContentController(JFrame frame) {
        this.frame = frame;
    }

    // MODIFIES: this
    // EFFECTS: creates a new frame controller if one currently doesn't exist; returns the current frame controller.
    public static FrameContentController getFrameContentController(JFrame frame) {
        if (thisInstance == null) {
            thisInstance = new FrameContentController(frame);
        }
        return thisInstance;
    }

    // MODIFIES: this
    // EFFECTS: removes all of the frame's previous content so new content can be added,
    //    then shows the given JPanel on this' JFrame
    public void showPanelOnFrame(JPanel panel) {
        frame.setVisible(false);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.repaint();
        frame.setVisible(true);
    }
}
