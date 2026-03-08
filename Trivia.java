
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Trivia extends JFrame{
    private String username;
    private JPanel cardPanel, welcomePanel, usernamePanel, gamePanel, resultPanel;
    private JButton startButton, nextButton, usernameButton, playButton, playAgainButton;
    private JTextField inputField;
    private CardLayout cardLayout;
    private JLabel questionLabel, resultLabel;
    private JButton[] optionButtons = new JButton[4];
    private int currentQuestionIdx, currentScore;
    private List<List<String>> questionPair;
    private List<Integer> correctAnswers;
    public Trivia(){
        currentQuestionIdx = 0;
        currentScore = 0;
        importQuestions();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        //JPanel cardPanel = new JPanel(new CardLayout()); // For making more

        // Welcome Page
        createWelcomePanel();
        // Username Page
        createUsernamePanel();
        // Game page
        createGamePanel();
        // Results screen
        createResultPanel();

        ButtonHandler handler = new ButtonHandler();
        startButton.addActionListener(handler);
        usernameButton.addActionListener(handler);
        playButton.addActionListener(handler);
        nextButton.addActionListener(handler);
        playAgainButton.addActionListener(handler);

        //add all panels to cardPanel
        add(cardPanel);
    }

    private class ButtonHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event){
            if(event.getSource() == startButton){
                cardLayout.show(cardPanel, "U");
            }
            else if (event.getSource() == playButton){
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if(event.getSource() == nextButton){
                //createResultPanel();
                cardLayout.show(cardPanel, "R");
            }
            else if (event.getSource() == usernameButton){
                username = inputField.getText();
                JOptionPane.showMessageDialog(null, "Welcome " + username + "!");
            }// Why null? =
            else if (event.getSource() == playAgainButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                loadNextQuestions();
                cardLayout.show(cardPanel, "W");
            }
        }
    }

    private class OptionButtonsHandler implements ActionListener{
        private int index;
        public OptionButtonsHandler(int index){
            this.index = index;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // validation
            if (index == correctAnswers.get(currentQuestionIdx)){
                // correct, give scores
                JOptionPane.showMessageDialog(null, "Correct!");
                currentScore++;
            }
            else{
                JOptionPane.showMessageDialog(null, "Incorrect!");
            }
            currentQuestionIdx++;
            loadNextQuestions();
        }
    }

    private void createWelcomePanel(){
        welcomePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("~Quizzie~", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 50));
        welcomePanel.add(title, BorderLayout.CENTER);
        startButton = new JButton("Start game");
        startButton.setFont(new Font("Arial", Font.BOLD, 35));
        welcomePanel.add(startButton, BorderLayout.SOUTH);
        cardPanel.add(welcomePanel, "W");
    }
    private void createUsernamePanel(){
        usernamePanel = new JPanel(new BorderLayout());
        inputField = new JTextField("Enter a username: ");
        inputField.setFont(new Font("Arial", Font.PLAIN, 50));

        usernameButton = new JButton("Save the name");
        usernameButton.setFont(new Font("Arial", Font.BOLD, 35));

        JPanel usernameButtonPanel = new JPanel(new FlowLayout());
        usernameButtonPanel.add(inputField);
        usernameButtonPanel.add(usernameButton);
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 35));
        buttonPanel.add(playButton);
        usernamePanel.add(buttonPanel, BorderLayout.SOUTH);
        cardPanel.add(usernamePanel, "U");
    }
    private void createGamePanel(){
        gamePanel = new JPanel(new BorderLayout());
        //gamePanel.add(new JLabel("Quiz Screen"));
        questionLabel = new JLabel("Question #1", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 32));
        gamePanel.add(questionLabel, BorderLayout.NORTH);
        //JLabel gameLabel = new JLabel("Quiz Screen");
        //gameLabel.setFont(new Font("Arial", Font.BOLD, 50));
        //gamePanel.add(gameLabel);
        nextButton = new JButton("Go to results");
        nextButton.setFont(new Font("Arial", Font.BOLD, 35));
        gamePanel.add(nextButton, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++){
            optionButtons[i] = new JButton("Option " + (i+1));
            OptionButtonsHandler optionButtonsHandler = new OptionButtonsHandler(i);
            optionButtons[i].addActionListener(optionButtonsHandler);
            buttonPanel.add(optionButtons[i]);
            //buttonPanel.add(optionButtons[i]);
        }
        gamePanel.add(buttonPanel, BorderLayout.CENTER);
        cardPanel.add(gamePanel, "G");
    }

    private void createResultPanel(){
        resultPanel = new JPanel(new BorderLayout());
        //resultPanel.add(new JLabel("Results Screen"));
        resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.BOLD, 50));
        //resultLabel.setText("Congrats " + username + " on earning " + currentScore + " pts!");
        resultPanel.add(resultLabel, BorderLayout.CENTER);

        playAgainButton = new JButton("Play again?");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 35));
        resultPanel.add(playAgainButton, BorderLayout.SOUTH);
        cardPanel.add(resultPanel, "R");
    }

    private void importQuestions(){
        questionPair = new ArrayList<>();
        correctAnswers = new ArrayList<>();

        questionPair.add(List.of("Question 1: What do we refer to BST in 4319?",
                "British Summer Time", "Breadth Search Tree", "Binary Search Tree", "None of the above"));
        correctAnswers.add(2);
        questionPair.add(List.of("Question 2: Which class belongs to Java Swing?",
                "NumberFormatException", "String", "Graphics", "None of the above"));
        correctAnswers.add(3);
        questionPair.add(List.of("Question 3: What is the capital of France?",
                "Paris", "London", "Berlin", "Rome"));
        correctAnswers.add(0);
        questionPair.add(List.of("Question 4: Which planet is known as the Red Planet?",
                "Earth", "Venus", "Mars", "Jupiter"));
        correctAnswers.add(2);
        questionPair.add(List.of("Question 5: Recursion always needs a?",
                "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
        questionPair.add(List.of("Question 1: What do we refer to BST in 4319?",
                "British Summer Time", "Breadth Search Tree", "Binary Search Tree", "None of the above"));
        correctAnswers.add(2);
        questionPair.add(List.of("Question 2: Which class belongs to Java Swing?",
                "NumberFormatException", "String", "Graphics", "None of the above"));
        correctAnswers.add(3);
        questionPair.add(List.of("Question 3: What is the capital of France?",
                "Paris", "London", "Berlin", "Rome"));
        correctAnswers.add(0);
        questionPair.add(List.of("Question 4: Which planet is known as the Red Planet?",
                "Earth", "Venus", "Mars", "Jupiter"));
        correctAnswers.add(2);
        questionPair.add(List.of("Question 5: Recursion always needs a?",
                "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
    }

    private void loadNextQuestions(){
        if (questionPair == null || currentQuestionIdx > questionPair.size() - 1){
            //createResultPanel();
            resultLabel.setText("Congrats " + username + " on earning " + currentScore + " pts!");
            cardLayout.show(cardPanel, "R");
            return;
        }
        questionLabel.setText((questionPair.get(currentQuestionIdx).get(0)));
        for (int i = 0; i < 4; i++){
            optionButtons[i].setText(questionPair.get(currentQuestionIdx).get(i + 1));
        }
    }
}
