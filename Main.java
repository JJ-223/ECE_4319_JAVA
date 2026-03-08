import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Trivia quiz = new Trivia();
        quiz.setTitle("Trivia Game by Jessie");
        quiz.setSize(800, 600);
        quiz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        quiz.setLocationRelativeTo(null);

        quiz.setVisible(true);
    }
}