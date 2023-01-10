package card;

import app.models.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


public class CollectionOfCards {
    @Getter
    @Setter(AccessLevel.PRIVATE)
    public ArrayList<Card> myCards;

    CollectionOfCards(){
        setMyCards(new ArrayList<Card>());
    }
    public void addCard(Card chosenCard){
        if(chosenCard == null){
            throw new NullPointerException("Tried to add Null to List.");
        }
        getMyCards().add(chosenCard);
    }
    public void addCards(ArrayList<Card> cards){
        if(cards == null){
            throw new NullPointerException("Tried to add Null to List.");
        }
        getMyCards().addAll(cards);
    }
    public void removeCard(Card chosenCard){
        int index = getMyCards().indexOf(chosenCard);
        if(index == -1){
            throw new IndexOutOfBoundsException("Tried to remove Card that is not in list.");
        }
        getMyCards().remove(index);
    }
}
