import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class PuzzleFragment {
    private int xStart;
    private int yStart;
    private int xFinish;
    private int yFinish;
    private BufferedImage image;

    public boolean wasPlaced() {
        if (yFinish < 600) {
            return true;
        }
        return false;
    }

    public PuzzleFragment(int xStart, int yStart, int xFinish, int yFinish, BufferedImage image) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xFinish = xFinish;
        this.yFinish = yFinish;
        this.image = image;
    }

    public int getxStart() {
        return xStart;
    }

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public int getyStart() {
        return yStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getxFinish() {
        return xFinish;
    }

    public void setxFinish(int xFinish) {
        this.xFinish = xFinish;
    }

    public int getyFinish() {
        return yFinish;
    }

    public void setyFinish(int yFinish) {
        this.yFinish = yFinish;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "PuzzleFragment{" +
                + xStart + " "
                + yStart + " " +
                + xFinish + " " +
                + yFinish + " " +
                '}';
    }
}
