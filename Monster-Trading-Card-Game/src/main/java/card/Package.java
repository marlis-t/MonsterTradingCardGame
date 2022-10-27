package card;

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
                System.out.println(e.getMessage());
            }
        }
    }

}
