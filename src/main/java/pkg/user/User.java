package pkg.user;

public class User {
    public String userName;
    public String HashedUserName;
    public String password;
    public boolean isLoggedIn=false;
    public String securityQuestion;
    public String answer;

    public User(String userName, String password,String securityQuestion,String answer,String hashedUserName) {
        this.userName = userName;
        this.password = password;
        this.securityQuestion= securityQuestion;
        this.answer = answer;
        this.HashedUserName = hashedUserName;
    }

    public User(String userName, String password,String hashedUserName) {
        this.userName = userName;
        this.HashedUserName = hashedUserName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHashedUserName() {
        return HashedUserName;
    }

    public void setHashedUserName(String hashedUserName) {
        HashedUserName = hashedUserName;
    }
}
