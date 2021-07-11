import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    private final static int EXAMPLE_IMAGE_X_START = 180;
    private final static int EXAMPLE_IMAGE_X_FINISH = EXAMPLE_IMAGE_X_START + EXAMPLE_IMAGE_WIDTH;
    private final static int EXAMPLE_IMAGE_Y_START = 20;
    private final static int EXAMPLE_IMAGE_Y_FINISH = EXAMPLE_IMAGE_Y_START + EXAMPLE_IMAGE_HEIGHT;

    private final static int RESULT_X_START = 20;
    private final static int RESULT_Y_START = 280;

    private final static int PUZZLES_MIN_SPACE = 5;
    private final static int PUZZLES_X_START = 690;
    private final static int PUZZLES_Y_START = RESULT_Y_START - PUZZLES_MIN_SPACE * 2;

    private final static int DEFAULT_NUMBER_OF_SECTIONS = 16;

    private final static int WIDTH_FOR_FRAGMENTS = 700;
    private int fragmentWidth = 160;
    private int fragmentHeight = 120;

    private final List<Line> lines = new LinkedList<>();
    private final List<ColoredRectangle> rectangles = new LinkedList<>();

    private ColoredRectangle activeAreaRectangle = new ColoredRectangle();
    private PuzzleFragment activePuzzleFragment;

    private BufferedImage image;
    private BufferedImage originalImage;
    private PuzzleFragment[] fragments;

    private JButton rotatePuzzle =  new JButton("Rotate puzzle");
    private JButton pullOutPuzzle =  new JButton("Pull out puzzle");
    private JLabel puzzleSolverLabel = new JLabel("Puzzle Solver");


    public PuzzleSolverWindow(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
            originalImage = ImageIO.read(imageFile);
            image = resizeImage(image, EXAMPLE_IMAGE_WIDTH, EXAMPLE_IMAGE_HEIGHT);
            originalImage = resizeImage(originalImage, STANDARD_WIDTH, STANDARD_HEIGHT);
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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseArea(e.getX(), e.getY());
                repaint();
            }
        });
    }

    private void initWindow(int puzzleNumber) {
        setLayout(null);
        setSize(1000, 1000);

        rotatePuzzle.setFocusable(false);
        rotatePuzzle.setBounds(1050, 200, 300, 50);
        rotatePuzzle.setBackground(Color.GREEN);
        add(rotatePuzzle);
        rotatePuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                activePuzzleFragment.rotate180();
                checkImageReadiness();
                repaint();
            }
        });

        pullOutPuzzle.setFocusable(false);
        pullOutPuzzle.setBounds(730, 200, 300, 50);
        pullOutPuzzle.setBackground(Color.GREEN);
        add(pullOutPuzzle);
        pullOutPuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                activePuzzleFragment.returnOldCoordinates();
                highlightPuzzle(activePuzzleFragment);
                repaint();
            }
        });

        puzzleSolverLabel.setBounds(600, 50, 900, 90);
        puzzleSolverLabel.setFont(new Font ("TimesRoman", Font.BOLD | Font.ITALIC, 80));
        add(puzzleSolverLabel);

        paintTemplateForSolving(puzzleNumber);
    }

    private void paintTemplateForSolving(int puzzleNumber){
        rectangles.add(new ColoredRectangle(RESULT_X_START, RESULT_Y_START, STANDARD_WIDTH, STANDARD_HEIGHT, Color.RED));
        for (int i = RESULT_X_START; i <= RESULT_X_START+STANDARD_WIDTH; i+=fragmentWidth) {
            lines.add(new Line(i, RESULT_Y_START, i, RESULT_Y_START+STANDARD_HEIGHT, Color.BLUE));
        }
        for (int i = RESULT_Y_START; i <= RESULT_Y_START+STANDARD_HEIGHT; i+=fragmentHeight) {
            lines.add(new Line(RESULT_X_START, i, RESULT_X_START+STANDARD_WIDTH, i, Color.BLUE));
        }
    }

    private void initializeFragments(File[] imageNamesFromFolder) throws IOException {
        System.out.println(Arrays.toString(imageNamesFromFolder));
        Arrays.sort(imageNamesFromFolder);
        System.out.println(Arrays.toString(imageNamesFromFolder));

        fragments = new PuzzleFragment[imageNamesFromFolder.length];

        int numberOfFragmentsInRowOrColumn = (int) Math.sqrt(imageNamesFromFolder.length);
        fragmentWidth = STANDARD_WIDTH / numberOfFragmentsInRowOrColumn;
        fragmentHeight = STANDARD_HEIGHT / numberOfFragmentsInRowOrColumn;

        int numberOfColumns = calculateNumberOfColumns(imageNamesFromFolder.length, fragmentWidth);
        int extraSpaceBeforeFragments = calculateExrtaSpace(numberOfColumns, fragmentWidth);

        for (int j = 0; j < imageNamesFromFolder.length; j += numberOfColumns) {
            for (int i = 0; i < numberOfColumns; i++) {
                int realCounter = j+i;
                BufferedImage image = ImageIO.read(imageNamesFromFolder[realCounter]);
                int x1 = extraSpaceBeforeFragments + PUZZLES_X_START + fragmentWidth * i + PUZZLES_MIN_SPACE * i;
                int y1 = PUZZLES_Y_START + fragmentHeight * (realCounter/numberOfColumns) + PUZZLES_MIN_SPACE * (realCounter/numberOfColumns);
                int x2 = extraSpaceBeforeFragments + PUZZLES_X_START + fragmentWidth * i + PUZZLES_MIN_SPACE * i + fragmentWidth;
                int y2 = PUZZLES_Y_START + fragmentHeight * (realCounter/numberOfColumns) + PUZZLES_MIN_SPACE * (realCounter/numberOfColumns) + fragmentHeight;
                PuzzleFragment puzzleFragment = new PuzzleFragment(x1, y1, x2, y2, image);
                fragments[realCounter] = puzzleFragment;
                System.out.println(realCounter);
                System.out.println(puzzleFragment);
            }
        }
        activePuzzleFragment = fragments[0];
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
            g2.setColor(line.color);
            g2.drawLine(line.x1, line.y1, line.x2, line.y2);
        }

        for (ColoredRectangle coloredRectangle : rectangles) {
            g2.setColor(coloredRectangle.getColor());
            g2.drawRect(coloredRectangle.getRectangle().x, coloredRectangle.getRectangle().y, coloredRectangle.getRectangle().width, coloredRectangle.getRectangle().height);
        }

        g2.setStroke(new BasicStroke(3));
        g2.setColor(activeAreaRectangle.getColor());
        g2.drawRect(activeAreaRectangle.getRectangle().x, activeAreaRectangle.getRectangle().y, activeAreaRectangle.getRectangle().width, activeAreaRectangle.getRectangle().height);
    }


    private void chooseArea(int x, int y){
        if(y > PUZZLES_Y_START){
            PuzzleFragment puzzleFragment = null;
            for (int i = 0; i < fragments.length; i++) {
                if(fragments[i].isOnCoordinates(x, y)){
                    puzzleFragment = fragments[i];
                    break;
                }
            }
            if(puzzleFragment!=null) {
                highlightPuzzle(puzzleFragment);
                return;
            }
        }
        if(x > RESULT_X_START && x < RESULT_X_START + STANDARD_WIDTH){
            if(y > RESULT_Y_START && y < RESULT_Y_START + STANDARD_HEIGHT){
                int newX = x - (x - RESULT_X_START) % fragmentWidth;
                int newY = y - (y - RESULT_Y_START) % fragmentHeight;
                checkOccupationOfPlace(newX, newY);
                activePuzzleFragment.moveTo(newX, newY);
                highlightPuzzle(activePuzzleFragment);
            }
            checkImageReadiness();
        }
    }

    private void highlightPuzzle(PuzzleFragment puzzleFragment){
        int width = puzzleFragment.getxFinish() - puzzleFragment.getxStart();
        int height = puzzleFragment.getyFinish() - puzzleFragment.getyStart();
        ColoredRectangle highlightRectangle = new ColoredRectangle(puzzleFragment.getxStart(),
                puzzleFragment.getyStart(), width, height, Color.GREEN);
        activeAreaRectangle = highlightRectangle;
        activePuzzleFragment = puzzleFragment;
    }

    private void checkOccupationOfPlace(int x, int y){
        for (int i = 0; i < fragments.length; i++) {
            if(fragments[i].getxStart()==x && fragments[i].getyStart()==y){
                fragments[i].returnOldCoordinates();
            }
        }
    }

    private boolean checkImageReadiness(){
        for (int i = 0; i < fragments.length; i++) {
            if(!fragments[i].wasPlaced()){
                return false;
            }
        }
        BufferedImage resultImage = getCombinedImage();
        if(checkIfTheSameImagesOptimized(originalImage, resultImage)){
        //if(checkIfTheSameImagesFull(originalImage, resultImage)){
            JOptionPane.showMessageDialog(this, "Congratulations! You won!");
            return true;
        }
        return false;
    }

    public BufferedImage getCombinedImage() {
        BufferedImage newImage = new BufferedImage(STANDARD_WIDTH, STANDARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        for (int i = 0; i < fragments.length; i++) {
            g2.drawImage(fragments[i].getImage(), null, fragments[i].getxStart()-RESULT_X_START, fragments[i].getyStart()-RESULT_Y_START);
        }
        g2.dispose();
        return newImage;
    }

    private boolean checkIfTheSameImagesOptimized(BufferedImage img1, BufferedImage img2){
        int[] dataBuffIntImg1 = img1.getRGB(0, 0, STANDARD_WIDTH, STANDARD_HEIGHT, null, 0, STANDARD_WIDTH);
        int[] dataBuffIntImg2 = img2.getRGB(0, 0, STANDARD_WIDTH, STANDARD_HEIGHT, null, 0, STANDARD_WIDTH);
        for (int i = 0; i < dataBuffIntImg1.length; i++) {
            if(dataBuffIntImg1[i]!=dataBuffIntImg2[i]){
                return false;
            }
        }
        return true;
    }

    private boolean checkIfTheSameImagesFull(BufferedImage img1, BufferedImage img2){
        int[] dataBuffIntImg1 = img1.getRGB(0, 0, STANDARD_WIDTH, STANDARD_HEIGHT, null, 0, STANDARD_WIDTH);
        int[] dataBuffIntImg2 = img2.getRGB(0, 0, STANDARD_WIDTH, STANDARD_HEIGHT, null, 0, STANDARD_WIDTH);
        for (int i = 0; i < dataBuffIntImg1.length; i++) {
            Color c1 = new Color(dataBuffIntImg1[i]);
            Color c2 = new Color(dataBuffIntImg2[i]);
            if((c1.getRed()!=c2.getRed() || c1.getGreen()!=c2.getGreen()) || c1.getBlue()!=c2.getBlue()){
                return false;
            }
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println();
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
