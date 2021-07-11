package objects;

import java.awt.*;

public class ColoredRectangle {
    private Rectangle rectangle;
    private Color color;

    public ColoredRectangle(int xStart, int yStart, int width, int height, Color color) {
        rectangle = new Rectangle(xStart, yStart, width, height);
        this.color = color;
    }

    public ColoredRectangle() {
        rectangle = new Rectangle(0,0,1,1);
        this.color = Color.WHITE;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Color getColor() {
        return color;
    }
}
