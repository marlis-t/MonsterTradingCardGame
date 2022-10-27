package user;

import card.CollectionOfCards;
import card.Deck;
import card.StackOfCards;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String username;
    private int coins;
    private int score;
    private int gamesPlayed;
    private String securityToken;

    Deck myDeck;
    StackOfCards myStack;

    User(String username) {
        //User exists already, connect to DB to get Information
        setUsername(username);
    }

    public void showUserData(){
        System.out.println(
                "Username: " + getUsername() + "\n" +
                "Coins: " + getCoins() + "\n" +
                "Score: " + getScore() + "\n" +
                "Games played: " + getGamesPlayed() + "\n"
        );
    }

    public void selectDeck(){
        System.out.println("Create new Deck? y/n: ");
        //scan answer
        //if yes: clear deck
        //if no: return
        myStack.showCards();
        System.out.println("\nType in the numbers of the Cards you want to select.\nYou can choose 4 Cards.\n");
        int[] list = new int[4];
        for(int i = 0; i < 4; i++){
            //scan input
            //check if input already in list
            //add Card at scanned index-1 to deck
            //add scanned index to list
        }

    }
}