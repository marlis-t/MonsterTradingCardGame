package cardrelated;

import java.util.*;

public class Deck {
    private List<Card> deckOfCards;
    private List<Card> getDeckOfCards(){
        return this.deckOfCards;
    }
    Deck(){}
    public void addCardToDeck(Card chosenCard){
        deckOfCards.add(chosenCard);
    }
    public void removeCardFromDeck(int index){
        deckOfCards.remove(index);
    }
    public void emptyDeck(){
        deckOfCards.clear();
    }

}
