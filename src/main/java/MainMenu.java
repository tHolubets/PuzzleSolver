import music.Mp3Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainMenu extends JPanel implements ActionListener {
    JButton createPuzzle = new JButton("Create new puzzle");
    JButton solvePuzzle = new JButton("Solve the puzzle");
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