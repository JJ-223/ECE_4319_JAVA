import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private static final int PORT = 8081;
    private static Connection connection;
    private static List<List<String>> serverQuestionPair;
    private static List<Integer> serverCorrectAnswers;

    public static void main(String[] args) throws SQLException {
        System.out.println("Game server started");
        connection = DBConnection.getConnection();
        try{
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port: " + PORT);
            while (true) {
                try{
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected: " + socket);
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                    String request = (String) inputStream.readObject();
                    String[] parts = request.split(":");
                    String action = parts[0];
                    if (action.equals("REGISTER") || action.equals("LOGIN")) {
                        String username = parts[1];
                        String password = parts[2];
                        System.out.println(action + "request from: " + username);
                        if(action.equals("REGISTER")){
                            User user = addUser(username, password);
                            if (user != null) {
                                outputStream.writeObject("SUCCESS: New user registered!");
                            }else{
                                outputStream.writeObject("FAIL: Duplicate username");
                            }
                        } else{
                            User user = getUser(username, password);
                            if (user != null) {
                                outputStream.writeObject("Welcome, " + user.getUsername() + "! Your highest score is "
                                        + user.getScore() + "\n Game started!");
                            } else {
                                outputStream.writeObject("Invalid username or password");
                            }
                        }
                    } else if (action.equals("START_GAME")) {
                        String category = parts[1];
                        importQuestions("src/questions/" + category + ".txt");
                        outputStream.writeObject(serverQuestionPair);
                        outputStream.writeObject(serverCorrectAnswers);
                        outputStream.flush();
                    } else if (action.equals("CHECK")){
                        int qIdx = Integer.parseInt(parts[1]);
                        int userAns = Integer.parseInt(parts[2]);
                        int correctAns = serverCorrectAnswers.get(qIdx);
                        outputStream.writeObject(userAns == correctAns);
                        outputStream.writeObject(correctAns);
                    } else if (action.equals("UPDATE_SCORE")){
                        String username = parts[1];
                        int newScore = Integer.parseInt(parts[2]);
                        updateUserScore(username, newScore);
                    } else if(action.equals("GET_LEADERBOARD")){
                        List<String> leaderboard = fetchTopScores();
                        outputStream.writeObject(leaderboard);
                    }
                    socket.close();
                }catch(Exception e){
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getUser(String username, String password){
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int score = resultSet.getInt("score");
                System.out.println("Welcome back " + username + "(highest score = " + score + ")");
                return new User(username, score);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static User addUser(String username, String password){
        String sql = "INSERT INTO users(username, password, score) VALUES (?, ?, 0)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            //ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.executeUpdate();
            System.out.println("New registration: " + username);
            return new User(username, 0);
        } catch(SQLException e){
            System.out.println("Error adding user: " + username + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void importQuestions(String filename){
        serverQuestionPair = new ArrayList<>();
        serverCorrectAnswers = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split("\\|");
                List<String> question  = new ArrayList<>();
                for(int i = 0; i < 5; i++){
                    question.add(parts[i]);
                }
                serverQuestionPair.add(question);
                serverCorrectAnswers.add(Integer.parseInt(parts[5]));
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void updateUserScore(String username, int newScore) {
        String sql = "UPDATE users SET score = ? WHERE username = ? AND score < ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newScore);
            pstmt.setString(2, username);
            pstmt.setInt(3, newScore);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<String> fetchTopScores() {
        List<String> topScores = new ArrayList<>();
        String sql = "SELECT username, score FROM users ORDER BY score DESC LIMIT 10";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                topScores.add(rs.getString("username") + ": " + rs.getInt("score") + " pts");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return topScores;
    }

}
