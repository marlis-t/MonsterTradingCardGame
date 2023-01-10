package card;

public class Deck extends CollectionOfCards {
   public Deck(){
       super();
   }
    public void emptyDeck(){
       //clears out deck
        getMyCards().clear();
    }

    public boolean isDeckEmpty(){
       if(getMyCards() == null){
           throw new NullPointerException("Deck does not exist!");
       }
       if(getMyCards().size() == 0){
           return true;
       }
       return false;
    }

}
