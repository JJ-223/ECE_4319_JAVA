
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Trivia extends JFrame{
    private String username;
    private JPanel cardPanel, welcomePanel, usernamePanel, gamePanel, resultPanel;
    private JButton startButton, nextButton, usernameButton, playButton, playAgainButton;
    private JButton japanButton, mexicoButton, australiaButton, taiwanButton, greeceButton, cambodiaButton;
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
        //importQuestions();

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
        //playButton.addActionListener(handler);
        nextButton.addActionListener(handler);
        playAgainButton.addActionListener(handler);

        japanButton.addActionListener(handler);
        mexicoButton.addActionListener(handler);
        australiaButton.addActionListener(handler);
        taiwanButton.addActionListener(handler);
        greeceButton.addActionListener(handler);
        cambodiaButton.addActionListener(handler);
        //add all panels to cardPanel
        add(cardPanel);
    }

    private class ButtonHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event){
            if(event.getSource() == startButton){
                cardLayout.show(cardPanel, "U");
            }
            /*else if (event.getSource() == playButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("Japan.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }*/
            else if(event.getSource() == nextButton){
                //createResultPanel();
                resultLabel.setText("Congrats " + username + " on earning " + currentScore + " pts!");
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
            else if (event.getSource() == japanButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/Japan.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == mexicoButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/Mexico.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == australiaButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/Australia.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == taiwanButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/Taiwan.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == greeceButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/Greece.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == cambodiaButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/Cambodia.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
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
        inputField.setFont(new Font("Arial", Font.PLAIN, 40));

        usernameButton = new JButton("Save the name");
        usernameButton.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel usernameButtonPanel = new JPanel(new FlowLayout());
        usernameButtonPanel.add(inputField);
        usernameButtonPanel.add(usernameButton);
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH);

        JPanel categoryPanel = new JPanel(new GridLayout(2,3,10, 10));
        japanButton = new JButton("Japan");                 japanButton.setFont(new Font("Arial", Font.BOLD, 35));
        mexicoButton = new JButton("Mexico");               mexicoButton.setFont(new Font("Arial", Font.BOLD, 35));
        australiaButton = new JButton("Australia");         australiaButton.setFont(new Font("Arial", Font.BOLD, 35));
        taiwanButton = new JButton("Taiwan");               taiwanButton.setFont(new Font("Arial", Font.BOLD, 35));
        greeceButton = new JButton("Greece");               greeceButton.setFont(new Font("Arial", Font.BOLD, 35));
        cambodiaButton = new JButton("Cambodia");           cambodiaButton.setFont(new Font("Arial", Font.BOLD, 35));
        categoryPanel.add(japanButton);
        categoryPanel.add(mexicoButton);
        categoryPanel.add(australiaButton);
        categoryPanel.add(taiwanButton);
        categoryPanel.add(greeceButton);
        categoryPanel.add(cambodiaButton);

        usernamePanel.add(categoryPanel, BorderLayout.CENTER);

        /*JPanel buttonPanel = new JPanel(new FlowLayout());
        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 35));
        buttonPanel.add(playButton);
        usernamePanel.add(buttonPanel, BorderLayout.SOUTH);*/
        cardPanel.add(usernamePanel, "U");
    }
    private void createGamePanel(){
        gamePanel = new JPanel(new BorderLayout());
        //gamePanel.add(new JLabel("Quiz Screen"));
        questionLabel = new JLabel("Question #1", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 35));
        gamePanel.add(questionLabel, BorderLayout.NORTH);
        //JLabel gameLabel = new JLabel("Quiz Screen");
        //gameLabel.setFont(new Font("Arial", Font.BOLD, 50));
        //gamePanel.add(gameLabel);
        nextButton = new JButton("Go to results");
        nextButton.setFont(new Font("Arial", Font.BOLD, 30));
        gamePanel.add(nextButton, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++){
            optionButtons[i] = new JButton("Option " + (i+1));
            optionButtons[i].setFont(new Font("Arial", Font.BOLD, 30));
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
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setVerticalAlignment(SwingConstants.CENTER);
        resultPanel.add(resultLabel, BorderLayout.CENTER);

        playAgainButton = new JButton("Play again?");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 35));
        resultPanel.add(playAgainButton, BorderLayout.SOUTH);
        cardPanel.add(resultPanel, "R");
    }

    private void importQuestions(String filename){
        questionPair = new ArrayList<>();
        correctAnswers = new ArrayList<>();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            while((line = reader.readLine()) != null){
                String[] parts = line.split("\\|");

                List<String> question  = new ArrayList<>();
                for(int i = 0; i < 5; i++){
                    question.add(parts[i]);
                }
                questionPair.add(question);
                correctAnswers.add(Integer.parseInt(parts[5]));
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

