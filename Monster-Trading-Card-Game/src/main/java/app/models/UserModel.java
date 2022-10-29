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
    @JsonAlias({"coins"})
    private int coins;
    @JsonAlias({"score"})
    private int score;
    @JsonAlias({"gamesPlayed"})
    private int gamesPlayed;

    public UserModel(){}
}
