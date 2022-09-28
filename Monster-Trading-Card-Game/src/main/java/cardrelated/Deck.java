package cardrelated;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Deck {
    @Getter
    private List<Card> deckOfCards;
    Deck(){
        this.deckOfCards = new ArrayList<Card>();
    }
    public void addCardToDeck(Card chosenCard){
        deckOfCards.add(chosenCard);
    }
    public boolean removeCardFromDeck(Card chosenCard){
        int index = deckOfCards.indexOf(chosenCard);
        if(index != -1){
            deckOfCards.remove(index);
            return true;
        }
        return false;
    }
    public void emptyDeck(){
        deckOfCards.clear();
    }

}
