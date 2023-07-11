package Model;

public class User {
    private String username;
    private String password;
    private String securityQuestion;
    private String securityAnswer;

    public User (String username, String password, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.securityAnswer = securityAnswer;
        this.securityQuestion = securityQuestion;
    }

    /**
     * Getter for username
     * @return the username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for password
     * @return the password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter method for password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for securityQuestion
     * @return the securityQuestion string
     */
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    /**
     * Setter method for securityQuestion
     * @param securityQuestion
     */
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    /**
     * Getter for securityAnswer
     * @return the securityAnswer string
     */
    public String getSecurityAnswer() {
        return securityAnswer;
    }

    /**
     * Setter method for securityAnswer
     * @param securityAnswer
     */
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
