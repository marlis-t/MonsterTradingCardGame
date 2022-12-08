package user;

import card.Card;
import card.Deck;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
import card.Package;
import card.StackOfCards;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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

    User(int userID, String username, int coins, int score, int gamesPlayed, StackOfCards myStack, TradingDeal myTradingDeal) {
        //User exists already, connect to DB to get Information
        setUserID(userID);
        setUsername(username);
        setCoins(coins);
        setScore(score);
        setGamesPlayed(gamesPlayed);
        setSecurityToken(securityToken);
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
        for(Card card: pack.getMyCards()){
            card.setUserID(getUserID());
        }
        myStack.addCards(pack.getMyCards());
    }
   public void setUpTradingDeal(int cardID, int minDamage, ELEMENT element, TYPE type){
        Card offerCard = null;
        for(Card card: myStack.getMyCards()){
            if(cardID == card.getCardID()){
                offerCard = card;
                break;
            }
        }
        if(offerCard != null){
            myTradingDeal = new TradingDeal(cardID, offerCard.getName(), offerCard.getDamage(), getUserID(), minDamage, element, type);
        }
   }
   public void assembleDeck(){
        //make sure deck is empty before assembling it
        myDeck.emptyDeck();
        ArrayList<Card> allCards = getMyStack().getMyCards();
        //sorts all Cards in Stack by damage, method in Card
        Collections.sort(allCards);
        //creates a sublist of Cards 0 (inc) to 5 (exc)
        ArrayList<Card> strongest5 = (ArrayList<Card>) allCards.subList(0, 5);
        myDeck.addCards(strongest5);
   }

}