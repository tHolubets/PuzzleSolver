package windows;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PuzzleAutoSolverWindow extends JPanel {
    BufferedImage image;

    public PuzzleAutoSolverWindow(BufferedImage image) {
        this.image = image;
        setLayout(null);
        setSize(1000, 1000);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g2.drawImage(image, 20, 15, null);
        }
    }
}
