package windows;

import marvin.image.MarvinImage;
import objects.Line;
import org.marvinproject.image.transform.scale.Scale;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class PuzzleCreatorWindow extends JPanel implements ActionListener {
    private final static String DIR_PATH_TEMPLATE = "./puzzles/";
    private final static String FILE_FORMAT = "png";
    private final String DIR_PATH;
    private final String FILE_PATH;

    private final static int IMAGE_WIDTH = 640;
    private final static int IMAGE_HEIGHT = 480;

    private final static int X_START = 20;
    private final static int X_FINISH = X_START + IMAGE_WIDTH;
    private final static int Y_START = 50;
    private final static int Y_FINISH = Y_START + IMAGE_HEIGHT;

    private final static int DEFAULT_NUMBER_OF_SECTIONS = 16;
    private final static int MIN_NUMBER_IN_PUZZLE_NAME = 1000;
    private final static int MAX_NUMBER_IN_PUZZLE_NAME = 9999;

    private final List<Line> lines = new LinkedList<>();

    private BufferedImage image;
    private JButton divideImage = new JButton("Divide the image");
    private JLabel folderPathLabel = new JLabel("Puzzles were saved in the folder: ");
    private JLabel sectionNChoiceLabel = new JLabel("Choose number of sections:");
    private ButtonGroup bg=new ButtonGroup();
    private JRadioButton sectionNChoice16RB = new JRadioButton("16 sections");
    private JRadioButton sectionNChoice25RB = new JRadioButton("25 sections");

    public PuzzleCreatorWindow(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
            image = resizeImage(image, IMAGE_WIDTH, IMAGE_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imageFileName = imageFile.getName();
        imageFileName = imageFileName.substring(0, imageFileName.indexOf("."));
        DIR_PATH = DIR_PATH_TEMPLATE + imageFileName;
        FILE_PATH = DIR_PATH + "/image";

        initWindow();
    }

    private void initWindow() {
        setLayout(null);
        setSize(1000, 1000);
        divideImage.setFocusable(false);
        divideImage.setBounds(300, 580, 300, 50);
        divideImage.setBackground(Color.GREEN);
        add(divideImage);
        divideImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int quantityOfParts = sectionNChoice25RB.isSelected() ? 25 : 16;
                divideImage(quantityOfParts);
                showDivision(quantityOfParts);
                changeWindowAfterDivision();
            }
        });

        sectionNChoiceLabel.setBounds(75,540,200,30);
        add(sectionNChoiceLabel);

        sectionNChoice16RB.setBounds(75,570,100,30);
        sectionNChoice16RB.setSelected(true);
        sectionNChoice25RB.setBounds(75,600,100,30);
        bg.add(sectionNChoice16RB);
        bg.add(sectionNChoice25RB);
        add(sectionNChoice16RB);
        add(sectionNChoice25RB);
    }

    private void divideImage(int quantityOfParts) {
        int sqrt = (int) Math.sqrt(quantityOfParts);
        divideImage(sqrt, sqrt);
    }

    private void divideImage(int columnQuantity, int rowQuantity) {
        int widthOfPart = image.getWidth() / columnQuantity;
        int heightOfPart = image.getHeight() / rowQuantity;

        BufferedImage[] parts = new BufferedImage[rowQuantity * columnQuantity];

        for (int i = 0; i < rowQuantity; i++) {
            for (int j = 0; j < columnQuantity; j++) {
                parts[(i * columnQuantity) + j] = image.getSubimage(
                        j * widthOfPart,
                        i * heightOfPart,
                        widthOfPart,
                        heightOfPart
                );
            }
        }

        savePartsOfImage(parts);
    }

    private void savePartsOfImage(BufferedImage[] parts) {
        File theDir = new File(DIR_PATH);
        deleteDir(theDir);
        theDir.mkdirs();
        try {
            for (int i = 0; i < parts.length; i++) {
                int randomNumber = MIN_NUMBER_IN_PUZZLE_NAME + (int)(Math.random() * ((MAX_NUMBER_IN_PUZZLE_NAME - MIN_NUMBER_IN_PUZZLE_NAME) + 1));
                File outputFile = new File(FILE_PATH + randomNumber + "." + FILE_FORMAT);
                BufferedImage imageForSaving = parts[i];
                if(randomNumber%2==0){
                    imageForSaving = rotate180(imageForSaving);
                }
                ImageIO.write(imageForSaving, FILE_FORMAT, outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
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

    private void showDivision(int quantityOfParts){
        int sqrt = (int) Math.sqrt(quantityOfParts);
        showDivision(sqrt, sqrt);
    }

    private void showDivision(int columnQuantity, int rowQuantity){
        int widthOfPart = image.getWidth() / columnQuantity;
        int heightOfPart = image.getHeight() / rowQuantity;

        for (int i = 1; i < rowQuantity; i++) {
            lines.add(new Line(X_START, Y_START + heightOfPart * i, X_FINISH, Y_START + heightOfPart * i, Color.RED));
        }

        for (int i = 1; i < columnQuantity; i++) {
            lines.add(new Line(X_START + widthOfPart * i, Y_START, X_START + widthOfPart * i, Y_FINISH, Color.RED));
        }

        repaint();
    }

    private void changeWindowAfterDivision(){
        folderPathLabel.setText(folderPathLabel.getText() + DIR_PATH);
        folderPathLabel.setBounds(200, 15, 300, 30);
        add(folderPathLabel);

        divideImage.setEnabled(false);
    }

    public BufferedImage rotate180(BufferedImage imageForRotation) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(180), imageForRotation.getWidth()/2, imageForRotation.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedImage = op.filter(imageForRotation, null);
        return rotatedImage;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            g2.drawImage(image, X_START, Y_START, null);
        }

        for (Line line : lines) {
            g.setColor(line.color);
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
