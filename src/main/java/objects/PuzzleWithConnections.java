package objects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class PuzzleWithConnections {
    private String name;
    private BufferedImage image;
    private BufferedImage rotatedImage;
    private PuzzleWithConnections right;
    private PuzzleWithConnections left;
    private PuzzleWithConnections top;
    private PuzzleWithConnections bottom;

    public PuzzleWithConnections(BufferedImage image, String name) {
        this.name = name;
        this.image = image;
        rotatedImage = rotate180(image);
    }

    public long evaluateImageSimilarityTopBottom(BufferedImage image2, int y1, int y2) {
        Color pixel1;
        Color pixel2;
        long sumOfDifference = 0;
        for (int x = 1; x < image.getWidth() - 1; x++) {
            pixel1 = new Color(image.getRGB(x, y1));
            pixel2 = new Color(image2.getRGB(x, y2));
            sumOfDifference += Math.abs(pixel1.getRed() - pixel2.getRed());
            sumOfDifference += Math.abs(pixel1.getGreen() - pixel2.getGreen());
            sumOfDifference += Math.abs(pixel1.getBlue() - pixel2.getBlue());
            sumOfDifference += Math.abs(pixel1.getAlpha() - pixel2.getAlpha());
        }
        return sumOfDifference;
    }

    public long evaluateImageSimilarityRightLeft(BufferedImage image2, int x1, int x2) {
        Color pixel1;
        Color pixel2;
        long sumOfDifference = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            pixel1 = new Color(image.getRGB(x1, y));
            pixel2 = new Color(image2.getRGB(x2, y));
            sumOfDifference += Math.abs(pixel1.getRed() - pixel2.getRed());
            sumOfDifference += Math.abs(pixel1.getGreen() - pixel2.getGreen());
            sumOfDifference += Math.abs(pixel1.getBlue() - pixel2.getBlue());
            sumOfDifference += Math.abs(pixel1.getAlpha() - pixel2.getAlpha());
        }
        return sumOfDifference;
    }

    public boolean isFree() {
        if ((right == null && left == null) && (top == null && bottom == null)) {
            return true;
        }
        return false;
    }

    public void setConnectionsToNull(){
        top = null;
        bottom = null;
        right = null;
        left = null;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public PuzzleWithConnections getRight() {
        return right;
    }

    public void setRight(PuzzleWithConnections right) {
        this.right = right;
    }

    public PuzzleWithConnections getLeft() {
        return left;
    }

    public void setLeft(PuzzleWithConnections left) {
        this.left = left;
    }

    public PuzzleWithConnections getTop() {
        return top;
    }

    public void setTop(PuzzleWithConnections top) {
        this.top = top;
    }

    public PuzzleWithConnections getBottom() {
        return bottom;
    }

    public void setBottom(PuzzleWithConnections bottom) {
        this.bottom = bottom;
    }

    public BufferedImage getRotatedImage() {
        return rotatedImage;
    }

    public void setRotatedImage(BufferedImage rotatedImage) {
        this.rotatedImage = rotatedImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private BufferedImage rotate180(BufferedImage bufferedImage) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(180), image.getWidth() / 2, image.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }

    public void rotate180() {
        BufferedImage tempImage = image;
        image = rotatedImage;
        rotatedImage = tempImage;
    }
}
