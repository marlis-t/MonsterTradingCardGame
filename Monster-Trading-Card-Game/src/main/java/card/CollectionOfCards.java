package card;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


public class CollectionOfCards {
    @Getter
    @Setter(AccessLevel.PRIVATE)
    protected ArrayList<Card> myCards;

    CollectionOfCards(){
        setMyCards(new ArrayList<Card>());
    }
    public void addCard(Card chosenCard){
        if(chosenCard == null){
            throw new NullPointerException("Tried to add Null to List.");
        }
        myCards.add(chosenCard);
    }

    public void removeCard(Card chosenCard){
        int index = myCards.indexOf(chosenCard);
        if(index == -1){
            throw new IndexOutOfBoundsException("Tried to remove Card that is not in list.");
        }
        myCards.remove(index);
    }
}
