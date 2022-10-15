package card;

import java.util.ArrayList;

public class Stack extends CollectionOfCards{
    Stack(){
        super();
    }

    public void addCards(ArrayList<Card> cards){
        if(cards == null){
            throw new NullPointerException("Tried to add Null to List.");
        }
        myCards.addAll(cards);
    }

}
