package user;

import card.Card;
import card.Deck;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
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
    //private String securityToken;
    @JsonAlias({"deck"})
    Deck myDeck;
    @JsonAlias({"stack"})
    StackOfCards myStack;
    @JsonAlias({"tradingDeal"})
    TradingDeal myTradingDeal;
    public User(String username, String password){
        //for registration and pushing to db
        setUsername(username);
        setPassword(password);
        setCoins(20);
        setScore(100);
        setGamesPlayed(0);
        setBio("");
        setImage("");
        setMyStack(new StackOfCards());
        setMyDeck(new Deck());
        setMyTradingDeal(null);
    }

    public User(int userID, String username, int coins, int score, int gamesPlayed, StackOfCards myStack, TradingDeal myTradingDeal) {
        //User exists already, connect to DB to get Information
        setUserID(userID);
        setUsername(username);
        setCoins(coins);
        setScore(score);
        setGamesPlayed(gamesPlayed);
        setMyDeck(new Deck());
        setMyStack(myStack);
        setMyTradingDeal(myTradingDeal);
    }

    public User(){}

    public String showUserData(){
        return "Username: " + getUsername() + "\n" + "Bio: " + getBio() + "\n" + "Image: " + getImage() + "\n";
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
        myStack.addCards(pack.getMyCards());
        setCoins(getCoins()-5);
        //push changes to db
    }
   public void setUpTradingDeal(int cardID, int minDamage, ELEMENT element, TYPE type){
        Card offerCard = null;
        for(Card card: getMyStack().getMyCards()){
            if(cardID == card.getCardID()){
                offerCard = card;
                card.setPaused(true);
                break;
            }
        }
        if(offerCard != null){
            myTradingDeal = new TradingDeal(0,cardID, getUserID(), offerCard.getName(), offerCard.getDamage(), minDamage, element, type);
            //push to db
            //id von trading deal, der zurückkommt
        }else{
            throw new IllegalArgumentException("Card to trade not found in stack");
        }
   }
   public void assembleDeck(){
        //make sure deck is empty before assembling it
        getMyDeck().emptyDeck();
        getMyDeck().addCards(getMyStack().best4Cards());
   }

}