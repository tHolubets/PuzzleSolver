package service;

import objects.PuzzleWithConnections;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PuzzleAutoSolver {
    private PuzzleWithConnections[] puzzles;
    private PuzzleWithConnections topLeftPuzzle;
    private PuzzleWithConnections topPuzzle;

    private long maxAcceptedValue = 0;


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

    public BufferedImage formResultImage() {
        BufferedImage finalImage = formResultImage(0);
        long bestValue = maxAcceptedValue;
        System.out.println(maxAcceptedValue);
        for (int i = 1; i < puzzles.length; i++) {
            deleteAllConnections();
            BufferedImage tempImage = formResultImage(i);
            if(maxAcceptedValue<bestValue){
                bestValue = maxAcceptedValue;
                finalImage = tempImage;
            }
            System.out.println(maxAcceptedValue);
        }
        System.out.println();
        return finalImage;
    }

    private BufferedImage formResultImage(int startIndex) {
        int rowsNumber = (int) (Math.sqrt(puzzles.length) + 0.5);
        int columnNumber = rowsNumber;
        int widthOfPuzzle = puzzles[startIndex].getImage().getWidth();
        int heightOfPuzzle = puzzles[startIndex].getImage().getHeight();

        long localMaxValue = formColumn(puzzles[startIndex], heightOfPuzzle, rowsNumber);
        long localMaxValue2 = formRow(topPuzzle, widthOfPuzzle, columnNumber);
        if (localMaxValue2 > localMaxValue) {
            localMaxValue = localMaxValue2;
        }
        PuzzleWithConnections tempTopPuzzle = topLeftPuzzle;
        for (int i = 0; i < columnNumber; i++) {
            if (tempTopPuzzle.getBottom() != null) {
                tempTopPuzzle = tempTopPuzzle.getRight();
                continue;
            }
            localMaxValue2 = formColumnDown(tempTopPuzzle, heightOfPuzzle, rowsNumber);
            if (localMaxValue2 > localMaxValue) {
                localMaxValue = localMaxValue2;
            }
            tempTopPuzzle = tempTopPuzzle.getRight();
        }

        maxAcceptedValue = localMaxValue;

        return getCombinedImage(widthOfPuzzle, heightOfPuzzle);
    }

    private long formColumn(PuzzleWithConnections basicPuzzle, int height, int rowsNumber) {
        long maxValue = -1;
        PuzzleWithConnections basicPuzzleTop = basicPuzzle;
        PuzzleWithConnections basicPuzzleBottom = basicPuzzle;

        for (int i = 0; i < rowsNumber - 1; i++) {
            int bestIndexTopBottom = -1;
            long bestResultTopBottom = Long.MAX_VALUE;
            boolean isBestRotated = false;
            boolean isBestBottom = false;
            for (int j = 0; j < puzzles.length; j++) {
                if (!puzzles[j].isFree() || puzzles[j].equals(basicPuzzleTop)) {
                    continue;
                }
                long topValue = basicPuzzleTop.evaluateImageSimilarityTopBottom(puzzles[j].getImage(), 0, height - 1);
                long topRotatedValue = basicPuzzleTop.evaluateImageSimilarityTopBottom(puzzles[j].getRotatedImage(), 0, height - 1);
                long bottomValue = basicPuzzleBottom.evaluateImageSimilarityTopBottom(puzzles[j].getImage(), height - 1, 0);
                long bottomRotatedValue = basicPuzzleBottom.evaluateImageSimilarityTopBottom(puzzles[j].getRotatedImage(), height - 1, 0);
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
            if (bestResultTopBottom > maxValue) {
                maxValue = bestResultTopBottom;
            }
            topPuzzle = basicPuzzleTop;
        }
        return maxValue;
    }

    private long formColumnDown(PuzzleWithConnections basicPuzzle, int height, int rowsNumber) {
        long maxValue = -1;
        PuzzleWithConnections basicPuzzleBottom = basicPuzzle;

        for (int i = 0; i < rowsNumber - 1; i++) {
            int bestIndex = -1;
            long bestResult = Long.MAX_VALUE;
            boolean isBestRotated = false;
            for (int j = 0; j < puzzles.length; j++) {
                if (!puzzles[j].isFree()) {
                    continue;
                }
                long bottomValue = basicPuzzleBottom.evaluateImageSimilarityTopBottom(puzzles[j].getImage(), height - 1, 0);
                long bottomRotatedValue = basicPuzzleBottom.evaluateImageSimilarityTopBottom(puzzles[j].getRotatedImage(), height - 1, 0);
                if (bottomValue < bestResult) {
                    bestResult = bottomValue;
                    bestIndex = j;
                    isBestRotated = false;
                }
                if (bottomRotatedValue < bestResult) {
                    bestResult = bottomRotatedValue;
                    bestIndex = j;
                    isBestRotated = true;
                }
            }
            if (isBestRotated) {
                puzzles[bestIndex].rotate180();
            }
            basicPuzzleBottom.setBottom(puzzles[bestIndex]);
            puzzles[bestIndex].setTop(basicPuzzleBottom);
            basicPuzzleBottom = puzzles[bestIndex];
            if (bestResult > maxValue) {
                maxValue = bestResult;
            }
        }
        return maxValue;
    }

    private long formRow(PuzzleWithConnections basicPuzzle, int width, int columnNumber) {
        long maxValue = -1;

        PuzzleWithConnections basicPuzzleLeft = basicPuzzle;
        PuzzleWithConnections basicPuzzleRight = basicPuzzle;

        for (int i = 0; i < columnNumber - 1; i++) {
            int bestIndexRightLeft = -1;
            long bestResultRightLeft = Long.MAX_VALUE;
            boolean isBestRotated = false;
            boolean isBestLeft = false;
            for (int j = 0; j < puzzles.length; j++) {
                if (!puzzles[j].isFree()) {
                    continue;
                }
                long rightValue = basicPuzzleRight.evaluateImageSimilarityRightLeft(puzzles[j].getImage(), width - 1, 0);
                long rightRotatedValue = basicPuzzleRight.evaluateImageSimilarityRightLeft(puzzles[j].getRotatedImage(), width - 1, 0);
                long leftValue = basicPuzzleLeft.evaluateImageSimilarityRightLeft(puzzles[j].getImage(), 0, width - 1);
                long leftRotatedValue = basicPuzzleLeft.evaluateImageSimilarityRightLeft(puzzles[j].getRotatedImage(), 0, width - 1);
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
            if (bestResultRightLeft > maxValue) {
                maxValue = bestResultRightLeft;
            }
            topLeftPuzzle = basicPuzzleLeft;
        }
        return maxValue;
    }

    private BufferedImage getCombinedImage(int widthOfFragment, int heightOfFragment) {
        BufferedImage newImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        PuzzleWithConnections tempBasic = topLeftPuzzle;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            g2.drawImage(tempBasic.getImage(), null, widthOfFragment * i, 0);
            PuzzleWithConnections tempBasic2 = tempBasic.getBottom();
            for (int j = 1; j < Integer.MAX_VALUE; j++) {
                if (tempBasic2 == null) {
                    break;
                }
                g2.drawImage(tempBasic2.getImage(), null, widthOfFragment * i, heightOfFragment * j);
                tempBasic2 = tempBasic2.getBottom();
            }
            tempBasic = tempBasic.getRight();
            if (tempBasic == null) {
                break;
            }
        }
        g2.dispose();
        return newImage;
    }

    private void deleteAllConnections() {
        for (int i = 0; i < puzzles.length; i++) {
            puzzles[i].setConnectionsToNull();
        }
    }
}
