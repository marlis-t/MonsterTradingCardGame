package card;

import java.util.ArrayList;

public class StackOfCards extends CollectionOfCards{
    public StackOfCards(){
        super();
    }

    public void addCards(ArrayList<Card> cards){
        if(cards == null){
            throw new NullPointerException("Tried to add Null to List.");
        }
        myCards.addAll(cards);
    }

}
