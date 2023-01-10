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
        //setting up a new user
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
        //User exists already, info from db
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