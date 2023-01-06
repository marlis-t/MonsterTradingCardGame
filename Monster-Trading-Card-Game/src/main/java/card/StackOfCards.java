package card;

import app.models.Card;

import java.util.ArrayList;
import java.util.Comparator;

public class StackOfCards extends CollectionOfCards{
    public StackOfCards(){
        super();
    }
    public ArrayList<Card> best4Cards(){
        ArrayList<Card> bestCards = new ArrayList<Card>();
        //sorts Cards in Stack by damage (desc.)
        getMyCards().sort(Comparator.comparing(Card::getDamage).reversed());
        //nr of chosen Cards
        int chosenNr = 0;
        //nr of Card in Stack
        int cardNr = 0;
        while(chosenNr < 4){
            if(cardNr == getMyCards().size()){
                //for the rare case of two or fewer cards in stack, one paused
                break;
            }
            if(!getMyCards().get(cardNr).isPaused()){
                bestCards.add(getMyCards().get(cardNr));
                chosenNr++;
            }
            cardNr++;
        }
        if(bestCards.size() == 0){
            //either error or stack empty
            throw new IllegalStateException("No cards chosen");
        }
        return bestCards;
    }

}
