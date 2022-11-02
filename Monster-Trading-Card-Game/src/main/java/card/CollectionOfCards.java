package card;

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
        myCards.add(chosenCard);
    }
    public void addCards(ArrayList<Card> cards){
        if(cards == null){
            throw new NullPointerException("Tried to add Null to List.");
        }
        myCards.addAll(cards);
    }
    public void removeCard(Card chosenCard){
        int index = myCards.indexOf(chosenCard);
        if(index == -1){
            throw new IndexOutOfBoundsException("Tried to remove Card that is not in list.");
        }
        myCards.remove(index);
    }

    public void showCards(){
        if(myCards.size() == 0){
            System.out.println("No Cards to show!\n");
            return;
        }
        int size = myCards.size();
        for(int i = 0; i < size; i++){
            System.out.println(i + ": ");
            myCards.get(i).showCard();
        }
    }
}
