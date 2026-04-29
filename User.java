import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private int score;
    private String password;
    // other fields, such as password
    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.score = 0;
    }
    public User(String username, int score) {
        this.username = username;
        this.score = score;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", score=" + score +
                '}';
    }
}
