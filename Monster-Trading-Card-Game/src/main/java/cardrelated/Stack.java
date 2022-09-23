package cardrelated;
import java.util.*;

public class Stack {
    private List<Card> allPlayerCards;

    public List<Card> getAllPlayerCards() {
        return this.allPlayerCards;
    }
    Stack(){
        this.allPlayerCards = new ArrayList<Card>();
    }

    public void addCardToStack(Card chosenCard){
        allPlayerCards.add(chosenCard);
    }
    public boolean removeCardFromStack(Card chosenCard){
        int index = allPlayerCards.indexOf(chosenCard);
        if(index != -1){
            allPlayerCards.remove(index);
            return true;
        }
        return false;
    }
    public boolean changeCardStatus(Card chosenCard, boolean isPaused){
        int index = allPlayerCards.indexOf(chosenCard);
        if(index != -1){
            allPlayerCards.get(index).setIsPaused(isPaused);
            return true;
        }
        return false;
    }

}
