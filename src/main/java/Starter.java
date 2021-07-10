import javax.swing.*;

public class Starter {
        public static void main(String[] args){
            JFrame window;
            window = new JFrame("Main menu");
            window.setSize(550, 400);
            window.add(new MainMenu());
            window.setLocationRelativeTo(null);
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
    }

}
