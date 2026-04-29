import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Trivia extends JFrame{
    private String username, password;
    private JPanel cardPanel, welcomePanel, usernamePanel, gamePanel, resultPanel, nextButtonPanel, leaderboardPanel;
    private JButton startButton, resultsButton, registerButton, playAgainButton, loginButton, skipButton, scoreButton;
    private JButton japanButton, mexicoButton, australiaButton, taiwanButton, greeceButton, cambodiaButton;
    private JTextField inputField;
    private JPasswordField passwordField;
    private CardLayout cardLayout;
    private JLabel questionLabel, resultLabel, InstructionLabel, timerLabel;
    private JButton[] optionButtons = new JButton[4];
    private int currentQuestionIdx, currentScore, timeLeft;
    private List<List<String>> questionPair;
    private List<Integer> correctAnswers;
    private Timer questionTimer;
    private ObjectOutputStream out; private ObjectInputStream in; private Socket socket;
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
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
        createLeaderboardPanel();

        ButtonHandler handler = new ButtonHandler();
        startButton.addActionListener(handler);
        registerButton.addActionListener(handler);
        loginButton.addActionListener(handler);
        //playButton.addActionListener(handler);
        resultsButton.addActionListener(handler);
        skipButton.addActionListener(handler);
        playAgainButton.addActionListener(handler);
        scoreButton.addActionListener(handler);

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
            else if(event.getSource() == resultsButton){
                stopQuestionTimer();
                resultLabel.setText("on earning " + currentScore + " pts, " + username + "!");
                cardLayout.show(cardPanel, "R");
            }
            else if (event.getSource() == registerButton || event.getSource() == loginButton){
                username = inputField.getText();
                password = new String(passwordField.getPassword());
                String action = (event.getSource() == registerButton) ? "REGISTER" : "LOGIN";
                try {
                    socket = new Socket("localhost", 8081);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                    out.writeObject(action + ":" + username + ":" + password);
                    String response = (String) in.readObject();
                    System.out.println("Server says: " + response);
                    if (response.contains("Welcome")) {
                        JOptionPane.showMessageDialog(null, "Welcome back, " + username + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                    } else if(response.contains("registered")){
                        JOptionPane.showMessageDialog(null, "Account created successfully for " + username + "!", "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        JOptionPane.showMessageDialog(null, "Needs unique username OR wrong username/password. Please try again.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection error: Server is offline.");
                }
            }
            else if(event.getSource() == skipButton) {
                stopQuestionTimer();
                try {
                    Socket socket = new Socket("localhost", 8081);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    out.writeObject("CHECK:" + currentQuestionIdx + ":-1");
                    in.readObject();
                    int correctIndex = (int) in.readObject();

                    for (int i = 0; i < optionButtons.length; i++) {
                        optionButtons[i].setBackground(i == correctIndex ? Color.GREEN : Color.RED);
                    }
                    Timer pause = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            currentQuestionIdx++;
                            loadNextQuestions();
                        }
                    });
                    pause.setRepeats(false);
                    pause.start();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (event.getSource() == playAgainButton){
                stopQuestionTimer();
                currentQuestionIdx = 0;
                currentScore = 0;
                cardLayout.show(cardPanel, "W");
            }
            else if (event.getSource() == japanButton || event.getSource() == mexicoButton ||
                    event.getSource() == australiaButton || event.getSource() == taiwanButton ||
                    event.getSource() == greeceButton || event.getSource() == cambodiaButton) {
                String category = ((JButton)event.getSource()).getText();
                currentQuestionIdx = 0;
                currentScore = 0;

                try {
                    Socket gameSocket = new Socket("localhost", 8081);
                    ObjectOutputStream gameOut = new ObjectOutputStream(gameSocket.getOutputStream());
                    ObjectInputStream gameIn = new ObjectInputStream(gameSocket.getInputStream());

                    gameOut.writeObject("START_GAME:" + category);
                    questionPair = (List<List<String>>) gameIn.readObject();
                    correctAnswers = (List<Integer>) gameIn.readObject();
                    gameSocket.close();
                    loadNextQuestions();
                    cardLayout.show(cardPanel, "G");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Server Error: Could not fetch questions for " + category);
                    e.printStackTrace();
                }
            }
            else if(event.getSource() == scoreButton){
                showLeaderboard();
                //cardLayout.show(cardPanel, "L");
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
            if(questionTimer != null && questionTimer.isRunning()){
                questionTimer.stop();
            }
            try {
                Socket socket = new Socket("localhost", 8081);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject("CHECK:" + currentQuestionIdx + ":" + index);

                boolean isCorrect = (boolean) in.readObject();
                int correctIndex = (int) in.readObject();

                if (isCorrect) {
                    optionButtons[index].setBackground(Color.GREEN);
                    currentScore++;
                } else {
                    optionButtons[index].setBackground(Color.RED);
                    optionButtons[correctIndex].setBackground(Color.GREEN); // Show them the right one
                }

                Timer pause = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for(JButton btn : optionButtons){
                            btn.setBackground(UIManager.getColor("Button.background"));
                            btn.setContentAreaFilled(true);
                            btn.setOpaque(true);
                        }

                        currentQuestionIdx++;
                        loadNextQuestions();
                    }
                });
                pause.setRepeats(false);
                pause.start();
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
        inputField = new JTextField("Username: ");
        inputField.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 40));

        passwordField = new JPasswordField("Password:");
        passwordField.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 40));

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 30));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 30));

        JPanel usernameButtonPanel = new JPanel(new FlowLayout());
        usernameButtonPanel.add(inputField);
        usernameButtonPanel.add(passwordField);
        usernameButtonPanel.add(registerButton);
        usernameButtonPanel.add(loginButton);
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH);

        JPanel categoryPanel = new JPanel(new GridLayout(2,3,10, 10));
        createCategoryButtons();
        categoryPanel.add(japanButton);         categoryPanel.add(mexicoButton);
        categoryPanel.add(australiaButton);     categoryPanel.add(taiwanButton);
        categoryPanel.add(greeceButton);        categoryPanel.add(cambodiaButton);
        usernamePanel.add(categoryPanel, BorderLayout.CENTER);

        JPanel instructPanel = new JPanel(new FlowLayout());
        InstructionLabel = new JLabel("~Choose a category to start quizzing~ (Register to save your score)");
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
        resultsButton = new JButton("Go to results");
        resultsButton.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 30));
        skipButton = new JButton("Skip");
        skipButton.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 30));
        nextButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nextButtonPanel.add(resultsButton);
        nextButtonPanel.add(skipButton);
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

        timerLabel = new JLabel("Question Time: 20s");
        timerLabel.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 30));
        nextButtonPanel.add(timerLabel);

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

        scoreButton = new JButton("View Scores");
        scoreButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));
        background.add(scoreButton, BorderLayout.SOUTH);

        resultPanel.add(background, BorderLayout.CENTER);
        cardPanel.add(resultPanel, "R");

        String[] columns = {"Rank", "Player", "Score"};
        tableModel = new DefaultTableModel(columns, 0);
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 20));
        leaderboardTable.setRowHeight(30);
    }
    private void loadNextQuestions(){
        for (JButton btn : optionButtons){
            btn.setBackground(UIManager.getColor("Button.background"));
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
        }
        if (questionPair == null || currentQuestionIdx > questionPair.size() - 1){
            stopQuestionTimer();
            resultLabel.setText("on earning " + currentScore + " pts, " + username + "!");
            cardLayout.show(cardPanel, "R");
            return;
        }
        if(questionTimer != null && questionTimer.isRunning()){
            questionTimer.stop();
        }
        timeLeft = 20;
        timerLabel.setText("Question Time: " + timeLeft + "s");

        questionLabel.setText((questionPair.get(currentQuestionIdx).get(0)));
        for (int i = 0; i < 4; i++){
            optionButtons[i].setText(questionPair.get(currentQuestionIdx).get(i + 1));
        }
        questionTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Question Time: " + timeLeft + "s");
                if(timeLeft <= 0){
                    questionTimer.stop();
                    try {
                        Socket socket = new Socket("localhost", 8081);
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        out.writeObject("CHECK:" + currentQuestionIdx + ":-1"); // -1 because no button was pressed
                        in.readObject();
                        int correctIndex = (int) in.readObject();
                        for (int i = 0; i < optionButtons.length; i++) {
                            optionButtons[i].setBackground(i == correctIndex ? Color.GREEN : Color.RED);
                        }
                        socket.close();
                    } catch (Exception ex) { ex.printStackTrace(); }
                    JOptionPane.showMessageDialog(null, "Time's up! No points~");
                    currentQuestionIdx++;
                    loadNextQuestions();
                }
            }
        });
        questionTimer.start();
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
    private void stopQuestionTimer() {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }
    }

    private void createLeaderboardPanel() {
        leaderboardPanel = new JPanel(new BorderLayout());
        leaderboardPanel.setBackground(new Color(255, 248, 204));

        JLabel header = new JLabel("TOP 10 LEADERBOARD", SwingConstants.CENTER);
        header.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 40));

        String[] columns = {"Rank", "Player", "Score"};
        tableModel = new DefaultTableModel(columns, 0);
        leaderboardTable = new JTable(tableModel);

        leaderboardTable.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 20));
        leaderboardTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);

        playAgainButton = new JButton("Play again?");
        playAgainButton.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 35));

        //JPanel bottomPanel = new JPanel();
        //bottomPanel.setBackground(new Color(255, 248, 204));
        //bottomPanel.add(playAgainButton, BorderLayout.SOUTH);

        leaderboardPanel.add(header, BorderLayout.NORTH);
        leaderboardPanel.add(scrollPane, BorderLayout.CENTER);
        leaderboardPanel.add(playAgainButton, BorderLayout.SOUTH);

        cardPanel.add(leaderboardPanel, "L");
    }

    private void showLeaderboard() {
        new Thread(() -> {
            try {
                if (username != null && !username.trim().isEmpty() && !username.equals("Username:")) {
                    Socket updateSocket = new Socket("localhost", 8081);
                    ObjectOutputStream updateOut = new ObjectOutputStream(updateSocket.getOutputStream());
                    updateOut.writeObject("UPDATE_SCORE:" + username + ":" + currentScore);
                    updateOut.flush();
                    updateSocket.close();
                }

                Socket lbSocket = new Socket("localhost", 8081);
                ObjectOutputStream lbOut = new ObjectOutputStream(lbSocket.getOutputStream());
                ObjectInputStream lbIn = new ObjectInputStream(lbSocket.getInputStream());

                lbOut.writeObject("GET_LEADERBOARD");
                lbOut.flush();

                List<String> topTen = (List<String>) lbIn.readObject();
                lbSocket.close();

                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);

                    for (int i = 0; i < topTen.size(); i++) {
                        String[] parts = topTen.get(i).split(": ");
                        tableModel.addRow(new Object[]{i + 1, parts[0], parts[1]});
                    }

                    cardLayout.show(cardPanel, "L");
                    cardPanel.revalidate();
                    cardPanel.repaint();
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
