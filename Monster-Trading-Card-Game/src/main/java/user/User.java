package user;

import card.Card;
import card.Deck;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
import card.Package;
import card.StackOfCards;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int userID;
    private String username;
    private int coins;
    private int score;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private String securityToken;

    Deck myDeck;
    StackOfCards myStack;
    TradingDeal myTradingDeal;
    User(int userID, String username){
        //for registration and pushing to db
        setUserID(userID);
        setUsername(username);
        setCoins(20);
        setScore(100);
        setGamesPlayed(0);
        setGamesWon(0);
        setGamesLost(0);
        setSecurityToken("");
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
        //setSecurityToken(securityToken);
        setMyDeck(new Deck());
        setMyStack(myStack);
        setMyTradingDeal(myTradingDeal);
    }

    public void showUserData(){
        System.out.println(
                "Username: " + getUsername() + "\n" +
                "Coins: " + getCoins() + "\n" +
                "Score: " + getScore() + "\n" +
                "Games played: " + getGamesPlayed() + "\n"
        );
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
            myTradingDeal = new TradingDeal(cardID, offerCard.getName(), offerCard.getDamage(), getUserID(), minDamage, element, type);
            //push to db
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