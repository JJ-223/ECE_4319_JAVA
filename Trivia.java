
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
    private JButton startButton, nextButton, usernameButton, playAgainButton;
    private JButton japanButton, mexicoButton, australiaButton, taiwanButton, greeceButton, cambodiaButton;
    private JTextField inputField;
    private CardLayout cardLayout;
    private JLabel questionLabel, resultLabel, InstructionLabel;
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
            else if(event.getSource() == nextButton){
                //createResultPanel();
                resultLabel.setText("on earning " + currentScore + " pts, " + username + "!");
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
                importQuestions("src/questions/Japan.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == mexicoButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/questions/Mexico.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == australiaButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/questions/Australia.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == taiwanButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/questions/Taiwan.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == greeceButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/questions/Greece.txt");
                loadNextQuestions();
                cardLayout.show(cardPanel, "G");
            }
            else if (event.getSource() == cambodiaButton){
                currentQuestionIdx = 0;
                currentScore = 0;
                importQuestions("src/questions/Cambodia.txt");
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
        title.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 100));
        //welcomePanel.add(title, BorderLayout.CENTER);
        startButton = new JButton("Start game");
        startButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));

        ImageIcon bgIcon = new ImageIcon("src/pictures/Background.png");
        JLabel background = new JLabel(bgIcon);
        background.setLayout(new BorderLayout());
        background.add(title, BorderLayout.CENTER);
        background.add(startButton, BorderLayout.SOUTH);
        welcomePanel.add(background, BorderLayout.CENTER);
        cardPanel.add(welcomePanel, "W");
    }
    private void createUsernamePanel(){
        usernamePanel = new JPanel(new BorderLayout());
        inputField = new JTextField("Enter a username: ");
        inputField.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 40));

        usernameButton = new JButton("Save the name");
        usernameButton.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 30));

        JPanel usernameButtonPanel = new JPanel(new FlowLayout());
        usernameButtonPanel.add(inputField);
        usernameButtonPanel.add(usernameButton);
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH);

        JPanel categoryPanel = new JPanel(new GridLayout(2,3,10, 10));
        createCategoryButtons();
        categoryPanel.add(japanButton);         categoryPanel.add(mexicoButton);
        categoryPanel.add(australiaButton);     categoryPanel.add(taiwanButton);
        categoryPanel.add(greeceButton);        categoryPanel.add(cambodiaButton);
        usernamePanel.add(categoryPanel, BorderLayout.CENTER);

        JPanel instructPanel = new JPanel(new FlowLayout());
        InstructionLabel = new JLabel("~Choose a category to start quizzing~");
        InstructionLabel.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 35));
        instructPanel.add(InstructionLabel);
        usernamePanel.add(instructPanel, BorderLayout.SOUTH);

        Color cream = new Color(255, 248, 204);
        usernamePanel.setBackground(cream);
        usernameButtonPanel.setBackground(cream);
        categoryPanel.setBackground(cream);
        instructPanel.setBackground(cream);

        cardPanel.add(usernamePanel, "U");
    }


    private void createGamePanel(){
        Color cream = new Color(255, 248, 204);

        gamePanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Question #1", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 35));
        gamePanel.add(questionLabel, BorderLayout.NORTH);
        nextButton = new JButton("Go to results");
        nextButton.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 30));
        JPanel nextButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nextButtonPanel.add(nextButton);
        gamePanel.add(nextButtonPanel, BorderLayout.SOUTH);
        //gamePanel.add(nextButton, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++){
            optionButtons[i] = new JButton("Option " + (i+1));
            optionButtons[i].setFont(new Font("Franklin Gothic Medium", Font.BOLD, 30));
            OptionButtonsHandler optionButtonsHandler = new OptionButtonsHandler(i);
            optionButtons[i].addActionListener(optionButtonsHandler);
            buttonPanel.add(optionButtons[i]);
        }
        gamePanel.add(buttonPanel, BorderLayout.CENTER);

        gamePanel.setBackground(cream);
        buttonPanel.setBackground(cream);
        nextButtonPanel.setBackground(cream);

        cardPanel.add(gamePanel, "G");
    }

    private void createResultPanel(){
        resultPanel = new JPanel(new BorderLayout());
        resultLabel = new JLabel();

        ImageIcon bgIcon = new ImageIcon("src/pictures/Congrats.png");
        JLabel background = new JLabel(bgIcon);
        background.setLayout(new BorderLayout());

        resultLabel.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 75));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setVerticalAlignment(SwingConstants.CENTER);
        background.add(resultLabel, BorderLayout.CENTER);

        playAgainButton = new JButton("Play again?");
        playAgainButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));
        background.add(playAgainButton, BorderLayout.SOUTH);

        resultPanel.add(background, BorderLayout.CENTER);
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
            resultLabel.setText("on earning " + currentScore + " pts, " + username + "!");
            cardLayout.show(cardPanel, "R");
            return;
        }
        questionLabel.setText((questionPair.get(currentQuestionIdx).get(0)));
        for (int i = 0; i < 4; i++){
            optionButtons[i].setText(questionPair.get(currentQuestionIdx).get(i + 1));
        }
    }
    private void createCategoryButtons(){
        ImageIcon JPbg = new ImageIcon("src/pictures/Japanbg.png");         ImageIcon MXbg = new ImageIcon("src/pictures/Mexicobg.png");
        ImageIcon CMbg = new ImageIcon("src/pictures/Cambodiabg.png");      ImageIcon GCbg = new ImageIcon("src/pictures/Greecebg.png");
        ImageIcon AUbg = new ImageIcon("src/pictures/Australiabg.png");     ImageIcon TWbg = new ImageIcon("src/pictures/Taiwanbg.png");
        japanButton = new JButton("Japan", JPbg);                 japanButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));
        mexicoButton = new JButton("Mexico", MXbg);               mexicoButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));
        australiaButton = new JButton("Australia", AUbg);         australiaButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));
        taiwanButton = new JButton("Taiwan", TWbg);               taiwanButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));
        greeceButton = new JButton("Greece", GCbg);               greeceButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));
        cambodiaButton = new JButton("Cambodia", CMbg);           cambodiaButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));

        japanButton.setHorizontalTextPosition(SwingConstants.CENTER);       japanButton.setVerticalTextPosition(SwingConstants.CENTER);
        mexicoButton.setHorizontalTextPosition(SwingConstants.CENTER);       mexicoButton.setVerticalTextPosition(SwingConstants.CENTER);
        cambodiaButton.setHorizontalTextPosition(SwingConstants.CENTER);       cambodiaButton.setVerticalTextPosition(SwingConstants.CENTER);
        taiwanButton.setHorizontalTextPosition(SwingConstants.CENTER);       taiwanButton.setVerticalTextPosition(SwingConstants.CENTER);
        greeceButton.setHorizontalTextPosition(SwingConstants.CENTER);       greeceButton.setVerticalTextPosition(SwingConstants.CENTER);
        australiaButton.setHorizontalTextPosition(SwingConstants.CENTER);       australiaButton.setVerticalTextPosition(SwingConstants.CENTER);
    }
}
