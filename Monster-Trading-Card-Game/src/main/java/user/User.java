package user;

import app.models.Card;
import card.Deck;
import card.Package;
import card.StackOfCards;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
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
    @JsonAlias({"bio"})
    private String bio;
    @JsonAlias({"image"})
    private String image;
    @JsonAlias({"authToken"})
    private String authToken;
    @JsonAlias({"deck"})
    Deck myDeck;
    @JsonAlias({"stack"})
    StackOfCards myStack;


    public User(String username, String password){
        //for registration and pushing to db
        setUsername(username);
        setCoins(20);
        setScore(100);
        setGamesPlayed(0);
        setBio("");
        setImage("");
        setAuthToken("");
        setMyStack(new StackOfCards());
        setMyDeck(new Deck());
    }

    public User(int userID, String username, int coins, int score, int gamesPlayed, String auth, StackOfCards myStack) {
        //User exists already, connect to DB to get Information
        setUserID(userID);
        setUsername(username);
        setCoins(coins);
        setScore(score);
        setGamesPlayed(gamesPlayed);
        setMyDeck(new Deck());
        setMyStack(myStack);

    }
    public User(){}

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
    public void buyPackage(Package pack){
        if(pack == null){
            throw new IllegalArgumentException("Tried to add Null to stack");
        }
        for(Card card: pack.getMyCards()){
            card.setUserID(getUserID());
        }
        getMyStack().addCards(pack.getMyCards());
        setCoins(getCoins()-5);
    }
   public void assembleDeck(){
        //make sure deck is empty before assembling it
        getMyDeck().emptyDeck();
        getMyDeck().addCards(getMyStack().best4Cards());
   }

}