package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardModel {
    @JsonAlias({"cardID"})
    private int cardID;
    @JsonAlias({"userID"})
    private int userID;
    @JsonAlias({"name"})
    private String name;
    @JsonAlias({"damage"})
    private int damage;
    @JsonAlias({"paused"})
    private boolean paused;

    public CardModel(){}
}
