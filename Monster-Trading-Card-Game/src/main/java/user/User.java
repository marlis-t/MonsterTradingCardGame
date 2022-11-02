package user;

import card.Card;
import card.Deck;
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
    //private String securityToken;

    Deck myDeck;
    StackOfCards myStack;
    Card myOffer;
    Demand myDemand;
    User(int userID, String username){
        setUserID(userID);
        setUsername(username);
        setCoins(20);
        setScore(100);
        setGamesPlayed(0);
        setMyStack(new StackOfCards());
        setMyDeck(new Deck());
        setMyOffer(null);
        setMyDemand(null);
    }

    User(int userID, String username, int coins, int score, int gamesPlayed, StackOfCards myStack, Card myOffer, Demand myDemand) {
        //User exists already, connect to DB to get Information
        setUserID(userID);
        setUsername(username);
        setCoins(coins);
        setScore(score);
        setGamesPlayed(gamesPlayed);
        //setSecurityToken(securityToken);
        setMyDeck(new Deck());
        setMyStack(new StackOfCards());
        setMyOffer(myOffer);
        setMyDemand(myDemand);
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
    public void makeOffer(Card offerCard, Demand demand){
        int index = myStack.getMyCards().indexOf(offerCard);
        myStack.getMyCards().get(index).setPaused(true);
        setMyOffer(offerCard);
        makeDemand(demand);
    }
    private void makeDemand(Demand demand) {
        setMyDemand(demand);
    }
    /*
    public void selectDeck(){
        Scanner scanner = new Scanner(System.in);
        //only ask if new deck should be created if deck is filled
        if(!myDeck.isDeckEmpty()) {
            System.out.println("Create new Deck? y/n: ");
            String answer = scanner.nextLine();
            if (answer == "n") {
                return;
            } else if (answer == "y") {
                myDeck.emptyDeck();
            }else {
                throw new IllegalArgumentException("Only [y]es or [n]o are possible options");
            }
        }
        myStack.showCards();
        System.out.println("\nType in the numbers of the Cards you want to select.\nYou can choose 4 Cards.");
        int[] list = new int[4];
        int nr;
        while(myDeck.getMyCards().size() < 4){
            //scan input
            // throws InputMismatchException if wrong input
            nr = scanner.nextInt();
            //check if input already in list
            if(Arrays.asList(list).contains(nr)){
                System.out.println("This card is already in the deck");
                continue;
            }
            //add scanned index to list
            list[myDeck.getMyCards().size()] = nr;
            myDeck.addCard(myStack.getMyCards().get(nr));
        }
    }*/
}