import java.awt.*;

public class ColoredRectangle {
    private Rectangle rectangle;
    private Color color;

    public ColoredRectangle(int xStart, int yStart, int width, int height, Color color) {
        rectangle = new Rectangle(xStart, yStart, width, height);
        this.color = color;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Color getColor() {
        return color;
    }
}
