package card;

import java.util.ArrayList;
import java.util.Comparator;

public class StackOfCards extends CollectionOfCards{
    public StackOfCards(){
        super();
    }
    public ArrayList<Card> best4Cards(){
        ArrayList<Card> bestCards = new ArrayList<Card>();
        myCards.sort(Comparator.comparing(Card::getDamage).reversed());
        for(int i = 0; i < 4; i++){
            bestCards.add(myCards.get(i));
        }
        return bestCards;
    }

}
