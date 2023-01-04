package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserModel {
    @JsonAlias({"userID"})
    private int userID;
    @JsonAlias({"username"})
    private String username;
    @JsonAlias({"password"})
    private String password;
    @JsonAlias({"coins"})
    private int coins;
    @JsonAlias({"score"})
    private int score;
    @JsonAlias({"gamesPlayed"})
    private int gamesPlayed;
    @JsonAlias({"bio"})
    private String bio;
    @JsonAlias({"image"})
    private String image;
    @JsonAlias({"authToken"})
    private String authToken;



    public UserModel(String username, String password){
        //for registration and pushing to db
        setUsername(username);
        setPassword(password);
        setCoins(20);
        setScore(100);
        setGamesPlayed(0);
        setBio("");
        setImage("");
        setAuthToken("");

    }

    public UserModel(int userID, String username, int coins, int score, int gamesPlayed) {
        //User exists already, connect to DB to get Information
        setUserID(userID);
        setUsername(username);
        setCoins(coins);
        setScore(score);
        setGamesPlayed(gamesPlayed);

    }
    public UserModel(){}

    public String showUserData(){
        return "{ \"Username\": \"" + getUsername() + "\"," +
                " \"Bio\": \"" + getBio() + "\"," +
                " \"Image\": \"" + getImage() + "\" }";
    }
    public String showScore(){
        return "{ \"Username\": \"" + getUsername() + "\", " +
                "\"Score\": \"" + getScore() + "\" }";
    }
    public String showUserStats(){
        return "Score: " + getScore() + "\n" + "Games played: " + getGamesPlayed() + "\n";
    }

}