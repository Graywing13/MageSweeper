package ui.utilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// this class makes JPanels that have an image background.
public class JPanelImageBackground extends JPanel {
    private final String backgroundImagePath;

    // EFFECTS: a constructor that saves a path to the desired image to this
    public JPanelImageBackground(String bkgImgPath) {
        backgroundImagePath = bkgImgPath;
    }

    // EFFECTS: draws the background of the JPanel as this' image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Image bkgImg = ImageIO.read(new File(backgroundImagePath));
            g.drawImage(bkgImg, 0, 0, null);
        } catch (IOException e) {
            g.setColor(new Color(0));
            e.printStackTrace();
        }
    }
}
