package card;

import app.models.Card;

public class Package extends CollectionOfCards{
    public Package(){
        super();
        CardGenerator generator = new CardGenerator();
        for(int i = 0; i < 5; i++){
            //generate Card
            Card tempCard = generator.generateCard();
            try{
                addCard(tempCard);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        //package with 5 random cards
    }

}
