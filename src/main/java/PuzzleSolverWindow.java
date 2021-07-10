import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PuzzleSolverWindow extends JPanel implements ActionListener {
    private final static String DIR_PATH_TEMPLATE = "./puzzles/";
    private final static String FILE_FORMAT = "png";
    private final String DIR_PATH;
    private final String FILE_PATH;

    private final static int STANDARD_WIDTH = 640;
    private final static int STANDARD_HEIGHT = 480;

    private final static int EXAMPLE_IMAGE_WIDTH = STANDARD_WIDTH/2;
    private final static int EXAMPLE_IMAGE_HEIGHT = STANDARD_HEIGHT/2;

    private final static int EXAMPLE_IMAGE_X_START = 20;
    private final static int EXAMPLE_IMAGE_X_FINISH = EXAMPLE_IMAGE_X_START + EXAMPLE_IMAGE_WIDTH;
    private final static int EXAMPLE_IMAGE_Y_START = 20;
    private final static int EXAMPLE_IMAGE_Y_FINISH = EXAMPLE_IMAGE_Y_START + EXAMPLE_IMAGE_HEIGHT;

    private final static int RESULT_X_START = 20;
    private final static int RESULT_Y_START = 280;

    private final static int PUZZLES_MIN_SPACE = 5;
    private final static int PUZZLES_X_START = 690;
    private final static int PUZZLES_X_FINISH = 1400;
    private final static int PUZZLES_Y_START = RESULT_Y_START - PUZZLES_MIN_SPACE * 2;

    private final static int DEFAULT_NUMBER_OF_SECTIONS = 16;

    private final static int WIDTH_FOR_FRAGMENTS = 700;

    private final List<Line> lines = new LinkedList<>();
    private final List<ColoredRectangle> rectangles = new LinkedList<>();

    private BufferedImage image;
    private PuzzleFragment[] fragments;

    private JButton divideImage = new JButton("Divide the image");
    private JLabel folderPathLabel = new JLabel("Puzzles were saved in the folder: ");
    private JLabel sectionNChoiceLabel = new JLabel("Choose number of sections:");
    private ButtonGroup bg = new ButtonGroup();
    private JRadioButton sectionNChoice16RB = new JRadioButton("16 sections");
    private JRadioButton sectionNChoice25RB = new JRadioButton("25 sections");

    public PuzzleSolverWindow(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
            image = resizeImage(image, EXAMPLE_IMAGE_WIDTH, EXAMPLE_IMAGE_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imageFileName = imageFile.getName();
        imageFileName = imageFileName.substring(0, imageFileName.indexOf("."));
        DIR_PATH = DIR_PATH_TEMPLATE + imageFileName;
        FILE_PATH = DIR_PATH + "/image";

        File folder = new File(DIR_PATH);
        File[] imageNamesFromFolder = folder.listFiles();
        try {
            initializeFragments(imageNamesFromFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initWindow(imageNamesFromFolder.length);
    }

    private void initWindow(int puzzleNumber) {
        setLayout(null);
        setSize(1000, 1000);

        paintTemplateForSolving(puzzleNumber);
    }

    private void paintTemplateForSolving(int puzzleNumber){
        rectangles.add(new ColoredRectangle(RESULT_X_START, RESULT_Y_START, STANDARD_WIDTH, STANDARD_HEIGHT, Color.RED));
    }

    private void initializeFragments(File[] imageNamesFromFolder) throws IOException {
        System.out.println(Arrays.toString(imageNamesFromFolder));
        Arrays.sort(imageNamesFromFolder);
        System.out.println(Arrays.toString(imageNamesFromFolder));

        fragments = new PuzzleFragment[imageNamesFromFolder.length];

        int numberOfFragmentsInRowOrColumn = (int) Math.sqrt(imageNamesFromFolder.length);
        int widthOfFragment = STANDARD_WIDTH / numberOfFragmentsInRowOrColumn;
        int heightOfFragment = STANDARD_HEIGHT / numberOfFragmentsInRowOrColumn;

        int numberOfColumns = calculateNumberOfColumns(imageNamesFromFolder.length, widthOfFragment);
        int extraSpaceBeforeFragments = calculateExrtaSpace(numberOfColumns, widthOfFragment);

        for (int j = 0; j < imageNamesFromFolder.length; j += numberOfColumns) {
            for (int i = 0; i < numberOfColumns; i++) {
                int realCounter = j+i;
                BufferedImage image = ImageIO.read(imageNamesFromFolder[realCounter]);
                int x1 = extraSpaceBeforeFragments + PUZZLES_X_START + widthOfFragment * i + PUZZLES_MIN_SPACE * i;
                int y1 = PUZZLES_Y_START + heightOfFragment * (realCounter/numberOfColumns) + PUZZLES_MIN_SPACE * (realCounter/numberOfColumns);
                int x2 = extraSpaceBeforeFragments + PUZZLES_X_START + widthOfFragment * i + PUZZLES_MIN_SPACE * i + widthOfFragment;
                int y2 = PUZZLES_Y_START + heightOfFragment * (realCounter/numberOfColumns) + PUZZLES_MIN_SPACE * (realCounter/numberOfColumns) + heightOfFragment;
                PuzzleFragment puzzleFragment = new PuzzleFragment(x1, y1, x2, y2, image);
                fragments[realCounter] = puzzleFragment;
                System.out.println(realCounter);
                System.out.println(puzzleFragment);
            }
        }
    }

    private int calculateExrtaSpace(int numberOfFragmentsInRow, int widthOfFragment) {
        int freeSpace = WIDTH_FOR_FRAGMENTS - numberOfFragmentsInRow * widthOfFragment;
        return freeSpace / 2;
    }

    private int calculateNumberOfColumns(int numberOfFragments, int widthOfFragment) {
        int totalWidth = numberOfFragments * (widthOfFragment + PUZZLES_MIN_SPACE);
        int numberOfRows = (int) ((totalWidth + WIDTH_FOR_FRAGMENTS * 0.99) / WIDTH_FOR_FRAGMENTS);
        int numberOfFragmentsInRow = numberOfFragments / numberOfRows;
        if (numberOfFragments % numberOfRows != 0) {
            numberOfFragmentsInRow++;
        }
        return numberOfFragmentsInRow;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g2.drawImage(image, EXAMPLE_IMAGE_X_START, EXAMPLE_IMAGE_Y_START, null);
        }

        for (int i = 0; i < fragments.length; i++) {
            g2.drawImage(fragments[i].getImage(), fragments[i].getxStart(), fragments[i].getyStart(), null);
        }

        for (Line line : lines) {
            g.setColor(line.color);
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
        }

        for (ColoredRectangle coloredRectangle : rectangles) {
            g.setColor(coloredRectangle.getColor());
            g.drawRect(coloredRectangle.getRectangle().x, coloredRectangle.getRectangle().y, coloredRectangle.getRectangle().width, coloredRectangle.getRectangle().height);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        MarvinImage image = new MarvinImage(originalImage);
        Scale scale = new Scale();
        scale.load();
        scale.setAttribute("newWidth", targetWidth);
        scale.setAttribute("newHeight", targetHeight);
        scale.process(image.clone(), image, null, null, false);
        return image.getBufferedImageNoAlpha();
    }
}
