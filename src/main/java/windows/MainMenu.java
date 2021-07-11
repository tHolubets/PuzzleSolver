package windows;

import service.PuzzleAutoSolver;
import windows.PuzzleCreatorWindow;
import windows.PuzzleSolverWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JPanel implements ActionListener {
    JButton createPuzzle = new JButton("Create new puzzle");
    JButton solvePuzzle = new JButton("Solve the puzzle");
    JButton automaticPuzzleSolving = new JButton("Automatic puzzle solving");
    JFileChooser chooser;

    public MainMenu() {
        setLayout(null);
        setSize(1000, 1000);
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        createPuzzle.setFocusable(false);
        createPuzzle.setBounds(20, 50, 500, 50);
        createPuzzle.setBackground(Color.GREEN);
        add(createPuzzle);
        createPuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chooser.setCurrentDirectory(new File("./images/"));
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    PuzzleCreatorWindow puzzleCreatorWindow = new PuzzleCreatorWindow(chooser.getSelectedFile());
                    JFrame window = new JFrame("Puzzle Creator");
                    window.add(puzzleCreatorWindow);
                    customizeWindow(window, 700, 680);
                }
            }
        });

        solvePuzzle.setFocusable(false);
        solvePuzzle.setBounds(20, 150, 500, 50);
        solvePuzzle.setBackground(Color.GREEN);
        add(solvePuzzle);
        solvePuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chooser.setCurrentDirectory(new File("./images/"));
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    PuzzleSolverWindow puzzleSolverWindow = new PuzzleSolverWindow(chooser.getSelectedFile());
                    JFrame window = new JFrame("Puzzle Solver");
                    window.add(puzzleSolverWindow);
                    customizeWindow(window, 1410, 1000);
                }
            }
        });

        automaticPuzzleSolving.setFocusable(false);
        automaticPuzzleSolving.setBounds(20, 250, 500, 50);
        automaticPuzzleSolving.setBackground(Color.GREEN);
        add(automaticPuzzleSolving);
        automaticPuzzleSolving.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chooser.setCurrentDirectory(new File("./images/"));
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    PuzzleAutoSolver autoSolver = new PuzzleAutoSolver(chooser.getSelectedFile());
                    BufferedImage[] autoResultImage = autoSolver.formResultImage();
                    File outputFile = new File("./images/fhf.png");
                    File outputFile2 = new File("./images/fhf2.png");
                    try {
                        ImageIO.write(autoResultImage[0], "png", outputFile);
                        ImageIO.write(autoResultImage[1], "png", outputFile2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void customizeWindow(JFrame window, int width, int height){
        window.setSize(width, height);
        window.setLocationRelativeTo(null);
        //window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}