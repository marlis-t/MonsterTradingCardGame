package battle;

import card.Card;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.Getter;
import lombok.Setter;
import user.User;

import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
public class Battle {
    private User User1;
    private User User2;
    private int round;

    private Random randomizer;

    private int card1Damage;
    private int card2Damage;

    public Battle(User User1, User User2){
        setUser1(User1);
        setUser2(User2);
        setRound(0);
        setRandomizer(new Random());
        setCard1Damage(0);
        setCard2Damage(0);
    }
    private Boolean shouldBattleContinue(){
        if(getUser1().getMyDeck().isDeckEmpty()){
            return false;
        }else if(getUser2().getMyDeck().isDeckEmpty()){
            return false;
        }else if(round >= 100){
            return false;
        }
        return true;
    }
    private Card getRandomCardFromUser(User user){
        ArrayList<Card> userCards = user.getMyDeck().getMyCards();
        //chooses random int in range of 0 to size of deck -1
        int randNr = getRandomizer().nextInt(userCards.size());
        return userCards.get(randNr);
    }

    private void addElementalDamage(Card card1, Card card2){
        if(card1.getElement() == ELEMENT.WATER){
            if(card2.getElement() == ELEMENT.FIRE){
                //super effective
                setCard1Damage(getCard1Damage()*2);
                setCard2Damage(getCard2Damage()/2);
            }else if(card2.getElement() == ELEMENT.NORMAL){
                //not effective
                setCard2Damage(getCard2Damage()*2);
                setCard1Damage(getCard1Damage()/2);
            }
        }else if(card1.getElement() == ELEMENT.FIRE){
            if(card2.getElement() == ELEMENT.NORMAL){
                //super effective
                setCard1Damage(getCard1Damage()*2);
                setCard2Damage(getCard2Damage()/2);
            }else if(card2.getElement() == ELEMENT.WATER){
                //not effective
                setCard2Damage(getCard2Damage()*2);
                setCard1Damage(getCard1Damage()/2);
            }
        }else if(card1.getElement() == ELEMENT.NORMAL){
            if(card2.getElement() == ELEMENT.WATER){
                //super effective
                setCard1Damage(getCard1Damage()*2);
                setCard2Damage(getCard2Damage()/2);
            }else if(card2.getElement() == ELEMENT.FIRE){
                //not effective
                setCard2Damage(getCard2Damage()*2);
                setCard1Damage(getCard1Damage()/2);
            }
        }
    }
    private void addMonsterSpecialities(Card card1, Card card2){
        if(card1.getName().contains("Goblin")){
            if(card2.getName().contains("Dragon")){
                //Goblin does not attack Dragon
                setCard1Damage(0);
            }
        }else if(card1.getName().contains("Dragon")){
            if(card2.getName().contains("Goblin")){
                //Goblin does not attack Dragon
                setCard2Damage(0);
            }else if(card2.getName().contains("FireElf")){
                //Dragon does not attack FireElf
                setCard1Damage(0);
            }
        }else if(card1.getName().contains("FireElf")){
            if(card2.getName().contains("Dragon")){
                //Dragon does not attack FireElf
                setCard2Damage(0);
            }
        }else if(card1.getName().contains("Wizard")){
            if(card2.getName().contains("Ork")){
                //Ork does not attack Wizard
                setCard2Damage(0);
            }
        }else if(card1.getName().contains("Ork")){
            if(card2.getName().contains("Wizard")){
                //Ork does not attack Wizard
                setCard1Damage(0);
            }
        }else if(card1.getName().contains("Knight")){
            if(card2.getName().equals("WaterMagicSpell")){
                //Knight does not attack WaterMagicSpell
                setCard1Damage(0);
            }
        }else if(card1.getName().equals("WaterMagicSpell")){
            if(card2.getName().contains("Knight")){
                //Knight does not attack WaterMagicSpell
                setCard2Damage(0);
            }
        }else if(card1.getName().contains("Kraken")){
            if(card2.getName().contains("Spell")){
                //(Any) Spell does not attack Kraken
                setCard2Damage(0);
            }
        }else if(card1.getName().contains("Spell")){
            if(card2.getName().contains("Kraken")){
                //(Any) Spell does not attack Kraken
                setCard1Damage(0);
            }
        }
    }

    private int whichCardStronger(Card card1, Card card2) {
        setCard1Damage(card1.getDamage());
        setCard2Damage(card2.getDamage());

        System.out.println("damage before: " + card1.getName() + ": " + getCard1Damage() + " " + card2.getName() + ": " + getCard2Damage());

        if (card1.getType() == TYPE.SPELL || card2.getType() == TYPE.SPELL) {
            //does not happen with 2 monster cards
            addElementalDamage(card1, card2);
        }
        if(card1.getType() == TYPE.MONSTER || card2.getType() == TYPE.MONSTER){
            //does not happen with 2 spell cards
            addMonsterSpecialities(card1, card2);
        }
        System.out.println("damage after: " + card1.getName() + ": " + getCard1Damage() + " " + card2.getName() + ": " + getCard2Damage());

        int strongerCard = 0;

        if(getCard1Damage() > getCard2Damage()){
            strongerCard = 1;
        }else if(getCard2Damage() > getCard1Damage()){
            strongerCard = 2;
        }else {
            strongerCard = 3;
        }
        System.out.println("result: " + strongerCard);
        return strongerCard;
    }

    private void changeCardOwner(Card card, User newOwner, User oldOwner) {
        if(!oldOwner.getMyDeck().getMyCards().remove(card)){
            //remove returns false if card not in deck
            throw new IllegalArgumentException("Deck does not contain Card, unable to remove");
        }
        if(oldOwner.getMyStack().getMyCards().remove(card)){
            //remove returns false if card not in stack
            throw new IllegalArgumentException("Stack does not contain Card, unable to remove");
        }
        newOwner.getMyDeck().addCard(card);
        newOwner.getMyStack().addCard(card);

        //call database to update UID of card
        System.out.println("User " + newOwner.getUsername() + " got Card " + card.getName() + " from User " + oldOwner.getUsername());

    }

    private void writeToLogfile(String content){

    }

    private void updatePlayerStats(){
        if(getUser1().getMyDeck().isDeckEmpty()){
            //User1 lost
            getUser1().setScore(getUser1().getScore()-5);
            getUser2().setScore(getUser1().getScore()+3);
            writeToLogfile("User '" + getUser2().getUsername() + "' won the battle\n");
        }else if(getUser2().getMyDeck().isDeckEmpty()){
            //User2 lost
            getUser2().setScore(getUser2().getScore()-5);
            getUser1().setScore(getUser1().getScore()+3);
            writeToLogfile("User '" + getUser1().getUsername() + "' won the battle\n");
        }else{
            writeToLogfile("The battle ended in a draw\n");
        }
        getUser1().setGamesPlayed(getUser1().getGamesPlayed()+1);
        getUser2().setGamesPlayed(getUser2().getGamesPlayed()+1);

        //update users in database
    }

    public void doBattle(){
        writeToLogfile("User '" + getUser1().getUsername() + "' and User '" + getUser2().getUsername() + "' are battling\n");
        while(shouldBattleContinue()){
            Card cardUser1 = getRandomCardFromUser(getUser1());
            Card cardUser2 = getRandomCardFromUser(getUser2());

            switch (whichCardStronger(cardUser1, cardUser2)) {
                case 1 -> {
                    changeCardOwner(cardUser2, getUser1(), getUser2());
                    writeToLogfile(
                            "Round: " + getRound() + "\n" +
                                    "Card '" + cardUser1.getName() +
                                    "' of User '" + getUser1().getUsername() + "' won\n"
                    );
                }
                case 2 -> {
                    changeCardOwner(cardUser1, getUser2(), getUser1());
                    writeToLogfile(
                            "Round: " + getRound() + "\n" +
                                    "Card '" + cardUser2.getName() +
                                    "' of User '" + getUser2().getUsername() + "' won\n"
                    );
                }
                case 3 -> writeToLogfile("Round: " + getRound() + " ended in a draw\n");
                default -> throw new IllegalArgumentException("WhichCardStronger() returned illegal argument");
            }

            setRound(getRound()+1);
        }
        updatePlayerStats();
    }


}
