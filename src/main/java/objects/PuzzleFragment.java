package objects;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class PuzzleFragment {
    private int xStart;
    private int yStart;
    private int xFinish;
    private int yFinish;
    private BufferedImage image;

    private int oldX;
    private int oldY;

    public boolean wasPlaced() {
        if (xFinish < 700) {
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

        oldX = xStart;
        oldY = yStart;
    }

    public boolean isOnCoordinates(int x, int y){
        if(x >= xStart && x <= xFinish){
            if(y >= yStart && y <= yFinish){
                return true;
            }
        }
        return false;
    }

    public void rotate180() {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(180), image.getWidth()/2, image.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        image = op.filter(image, null);
    }

    public void moveTo(int newX, int newY){
        int width = xFinish - xStart;
        int height = yFinish - yStart;
        xStart = newX;
        yStart = newY;
        xFinish = xStart + width;
        yFinish = yStart + height;
    }

    public void returnOldCoordinates(){
        int width = xFinish - xStart;
        int height = yFinish - yStart;
        xStart = oldX;
        yStart = oldY;
        xFinish = xStart + width;
        yFinish = yStart + height;
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
        return "objects.PuzzleFragment{" +
                + xStart + " "
                + yStart + " " +
                + xFinish + " " +
                + yFinish + " " +
                '}';
    }
}
