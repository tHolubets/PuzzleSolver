package service;

import objects.PuzzleFragment;
import objects.PuzzleWithConnections;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PuzzleAutoSolver {
    private PuzzleWithConnections[] puzzles;
    private PuzzleWithConnections leftPuzzle;
    private PuzzleWithConnections topPuzzle;


    public PuzzleAutoSolver(File imageFile) {
        String imageFileName = imageFile.getName();
        imageFileName = imageFileName.substring(0, imageFileName.indexOf("."));
        String dirPath = "./puzzles/" + imageFileName;

        File folder = new File(dirPath);
        File[] imageNamesFromFolder = folder.listFiles();

        try {
            initializePuzzles(imageNamesFromFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        topPuzzle = puzzles[0];
    }

    private void initializePuzzles(File[] imageNamesFromFolder) throws IOException {
        puzzles = new PuzzleWithConnections[imageNamesFromFolder.length];
        for (int j = 0; j < imageNamesFromFolder.length; j++) {
            BufferedImage tempImage = ImageIO.read(imageNamesFromFolder[j]);
            PuzzleWithConnections puzzleWithConnections = new PuzzleWithConnections(tempImage, imageNamesFromFolder[j].getName());
            puzzles[j] = puzzleWithConnections;
        }
    }

    public BufferedImage[] formResultImage() {
        int rowsNumber = (int) (Math.sqrt(puzzles.length) + 0.5);
        int columnNumber = rowsNumber;
        int widthOfPuzzle = puzzles[0].getImage().getWidth();
        int heightOfPuzzle = puzzles[0].getImage().getHeight();

        PuzzleWithConnections basicPuzzleTop = puzzles[0];
        PuzzleWithConnections basicPuzzleBottom = puzzles[0];

        for (int i = 0; i < rowsNumber-1; i++) {
            int bestIndexTopBottom = -1;
            long bestResultTopBottom = Long.MAX_VALUE;
            boolean isBestRotated = false;
            boolean isBestBottom = false;
            for (int j = 0; j < puzzles.length; j++) {
                if (!puzzles[j].isFree() || puzzles[j].equals(basicPuzzleTop)) {
                    continue;
                }
                long topValue = basicPuzzleTop.evaluateImageSimilarityTopBottom(puzzles[j].getImage(), 0, heightOfPuzzle - 1);
                long topRotatedValue = basicPuzzleTop.evaluateImageSimilarityTopBottom(puzzles[j].getRotatedImage(), 0, heightOfPuzzle - 1);
                long bottomValue = basicPuzzleBottom.evaluateImageSimilarityTopBottom(puzzles[j].getImage(), heightOfPuzzle - 1, 0);
                long bottomRotatedValue = basicPuzzleBottom.evaluateImageSimilarityTopBottom(puzzles[j].getRotatedImage(), heightOfPuzzle - 1, 0);
                System.out.println("BasicTop: " + basicPuzzleTop.getName());
                System.out.println(puzzles[j].getName() + " (top) = " + topValue);
                System.out.println(puzzles[j].getName() + " (topRotated) = " + topRotatedValue);
                System.out.println("BasicBottom: " + basicPuzzleBottom.getName());
                System.out.println(puzzles[j].getName() + " (bottom) = " + bottomValue);
                System.out.println(puzzles[j].getName() + " (bottomRotated) = " + bottomRotatedValue);
                if (topValue < bestResultTopBottom) {
                    bestResultTopBottom = topValue;
                    bestIndexTopBottom = j;
                    isBestRotated = false;
                    isBestBottom = false;
                }
                if (topRotatedValue < bestResultTopBottom) {
                    bestResultTopBottom = topRotatedValue;
                    bestIndexTopBottom = j;
                    isBestRotated = true;
                    isBestBottom = false;
                }
                if (bottomValue < bestResultTopBottom) {
                    bestResultTopBottom = bottomValue;
                    bestIndexTopBottom = j;
                    isBestRotated = false;
                    isBestBottom = true;
                }
                if (bottomRotatedValue < bestResultTopBottom) {
                    bestResultTopBottom = bottomRotatedValue;
                    bestIndexTopBottom = j;
                    isBestRotated = true;
                    isBestBottom = true;
                }
            }
            if (isBestRotated) {
                puzzles[bestIndexTopBottom].rotate180();
            }
            if (isBestBottom) {
                basicPuzzleBottom.setBottom(puzzles[bestIndexTopBottom]);
                puzzles[bestIndexTopBottom].setTop(basicPuzzleBottom);
                basicPuzzleBottom = puzzles[bestIndexTopBottom];
            } else {
                basicPuzzleTop.setTop(puzzles[bestIndexTopBottom]);
                puzzles[bestIndexTopBottom].setBottom(basicPuzzleTop);
                basicPuzzleTop = puzzles[bestIndexTopBottom];
            }
            topPuzzle = basicPuzzleTop;
        }


        /////////////////////////////////////////////////////////////////


        PuzzleWithConnections basicPuzzleLeft = puzzles[0];
        PuzzleWithConnections basicPuzzleRight = puzzles[0];

        System.out.println("\n\n\n\nStart\n\n\n");
        for (int i = 0; i < columnNumber-1; i++) {
            int bestIndexRightLeft = -1;
            long bestResultRightLeft = Long.MAX_VALUE;
            boolean isBestRotated = false;
            boolean isBestLeft = false;
            for (int j = 0; j < puzzles.length; j++) {
                /*if (!puzzles[j].isFree()) {
                    continue;
                }*/
                long rightValue = basicPuzzleRight.evaluateImageSimilarityRightLeft(puzzles[j].getImage(), widthOfPuzzle - 1, 0);
                long rightRotatedValue = basicPuzzleRight.evaluateImageSimilarityRightLeft(puzzles[j].getRotatedImage(), widthOfPuzzle - 1, 0);
                long leftValue = basicPuzzleLeft.evaluateImageSimilarityRightLeft(puzzles[j].getImage(), 0, widthOfPuzzle - 1);
                long leftRotatedValue = basicPuzzleLeft.evaluateImageSimilarityRightLeft(puzzles[j].getRotatedImage(), 0, widthOfPuzzle - 1);
                System.out.println("BasicRight: " + basicPuzzleRight.getName());
                System.out.println(puzzles[j].getName() + " (right) = " + rightValue);
                System.out.println(puzzles[j].getName() + " (rightRotated) = " + rightRotatedValue);
                System.out.println("BasicLeft: " + basicPuzzleLeft.getName());
                System.out.println(puzzles[j].getName() + " (left) = " + leftValue);
                System.out.println(puzzles[j].getName() + " (leftRotated) = " + leftRotatedValue);
                if (rightValue < bestResultRightLeft) {
                    bestResultRightLeft = rightValue;
                    bestIndexRightLeft = j;
                    isBestRotated = false;
                    isBestLeft = false;
                }
                if (rightRotatedValue < bestResultRightLeft) {
                    bestResultRightLeft = rightRotatedValue;
                    bestIndexRightLeft = j;
                    isBestRotated = true;
                    isBestLeft = false;
                }
                if (leftValue < bestResultRightLeft) {
                    bestResultRightLeft = leftValue;
                    bestIndexRightLeft = j;
                    isBestRotated = false;
                    isBestLeft = true;
                }
                if (leftRotatedValue < bestResultRightLeft) {
                    bestResultRightLeft = leftRotatedValue;
                    bestIndexRightLeft = j;
                    isBestRotated = true;
                    isBestLeft = true;
                }
            }
            if (isBestRotated) {
                puzzles[bestIndexRightLeft].rotate180();
            }
            if (isBestLeft) {
                basicPuzzleLeft.setLeft(puzzles[bestIndexRightLeft]);
                puzzles[bestIndexRightLeft].setRight(basicPuzzleLeft);
                basicPuzzleLeft = puzzles[bestIndexRightLeft];
            } else {
                basicPuzzleRight.setRight(puzzles[bestIndexRightLeft]);
                puzzles[bestIndexRightLeft].setLeft(basicPuzzleRight);
                basicPuzzleRight = puzzles[bestIndexRightLeft];
            }
            leftPuzzle = basicPuzzleLeft;
        }

        BufferedImage[] bufferedImages = new BufferedImage[2];
        bufferedImages[0] = getCombinedImage();
        bufferedImages[1] = getCombinedImage2();
        return bufferedImages;
    }

    private BufferedImage getCombinedImage() {
        BufferedImage newImage = new BufferedImage(640, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        PuzzleWithConnections tempBasic = leftPuzzle;
        for (int i = 0; i < 5; i++) {
            g2.drawImage(tempBasic.getImage(), null, 160*i, 0);
            tempBasic = tempBasic.getRight();
            if (tempBasic == null) {
                break;
            }
        }
        g2.dispose();
        return newImage;
    }

    private BufferedImage getCombinedImage2() {
        BufferedImage newImage = new BufferedImage(160, 480, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        PuzzleWithConnections tempBasic = topPuzzle;
        for (int i = 0; i < 5; i++) {
            g2.drawImage(tempBasic.getImage(), null, 0, 120 * i);
            tempBasic = tempBasic.getBottom();
            if (tempBasic == null) {
                break;
            }
        }
        g2.dispose();
        return newImage;
    }
}
