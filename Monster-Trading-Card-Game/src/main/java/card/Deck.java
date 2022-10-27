package card;

public class Deck extends CollectionOfCards {
   public Deck(){
       super();
   }
    public void emptyDeck(){
        myCards.clear();
    }

    public boolean isDeckEmpty(){
       if(myCards == null){
           throw new NullPointerException("Deck does not exist!");
       }
       if(myCards.size() == 0){
           return true;
       }
       return false;
    }

}
