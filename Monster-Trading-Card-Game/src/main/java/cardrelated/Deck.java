package cardrelated;

import java.util.*;

public class Deck {
    private List<Card> deckOfCards;
    private List<Card> getDeckOfCards(){
        return this.deckOfCards;
    }
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
