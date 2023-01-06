package battle;

import app.controllers.BattleDirectController;
import app.models.Card;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.Getter;
import lombok.Setter;
import user.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

@Getter
@Setter
public class Battle {
    private BattleDirectController battleDirectController;
    private User User1;
    private User User2;
    private int round;

    private Random randomizer;

    private int card1Damage;
    private int card2Damage;

    public Battle(User User1, User User2, BattleDirectController battleDirectController){
        setUser1(User1);
        setUser2(User2);
        setRound(0);
        setRandomizer(new Random());
        setCard1Damage(0);
        setCard2Damage(0);
        setBattleDirectController(battleDirectController);
    }
    public Boolean shouldBattleContinue(){
        if(getUser1().getMyDeck().isDeckEmpty()){
            return false;
        }else if(getUser2().getMyDeck().isDeckEmpty()){
            return false;
        }else return round < 100;
    }
    public Card getRandomCardFromUser(User user){
        ArrayList<Card> userCards = user.getMyDeck().getMyCards();
        //chooses random int in range of 0 to size of deck -1
        //throws IllegalArgumentException if deck is empty (size == 0)
        // bc battle should have ended already
        int randNr = getRandomizer().nextInt(userCards.size());
        Card chosenCard = userCards.get(randNr);
        if(chosenCard == null){
            throw new IndexOutOfBoundsException("Chosen Index points to Card not in Deck");
        }
        return chosenCard;
    }

    public Card getMutationCardFromUser(User user, Card basisCard){
        int damage = 0;
        int waterCount = 0;
        int fireCount = 0;
        int normalCount = 0;
        int monsterCount = 0;
        int spellCount = 0;

        //get 4 best cards from user stack + basisCard
        ArrayList<Card> bestCards = user.getMyStack().best4Cards();
        bestCards.add(basisCard);
        for(Card chosenCard : bestCards){
            //collect count of elements
            if(chosenCard.getElement() == ELEMENT.FIRE){
                fireCount++;
            }else if(chosenCard.getElement() == ELEMENT.WATER){
                waterCount++;
            }else{
                normalCount++;
            }
            //collect count of types
            if(chosenCard.getType() == TYPE.MONSTER){
                monsterCount++;
            }else{
                spellCount++;
            }
            //collect damage
            damage += chosenCard.getDamage();
        }

        //give name based on most often collected values
        String name;
        if(fireCount > waterCount && fireCount > normalCount){
            name = "Fire";
        }else if(waterCount > normalCount && waterCount > fireCount){
            name = "Water";
        }else{
            name = "Normal";
        }
        if(monsterCount > spellCount){
            name += "MutationMonster";
        }else{
            name += "MutationSpell";
        }

        return new Card("0", user.getUserID(), name, damage, false);
    }

    public void addElementalDamage(Card card1, Card card2){
        //here all cards need to have an element
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
    public void addMonsterSpecialities(Card card1, Card card2){
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
            if(card2.getName().equals("WaterSpell")){
                //Knight does not attack WaterMagicSpell
                setCard1Damage(0);
            }
        }else if(card1.getName().equals("WaterSpell")){
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

    public int whichCardStronger(Card card1, Card card2) {
        setCard1Damage(card1.getDamage());
        setCard2Damage(card2.getDamage());
        try {
            writeToLogfile("Card " + card1.getName() + " and Card " + card2.getName() + " have been chosen.\n");
            writeToLogfile("Damage (before type and specification includes) : " + card1.getName() + "-> " + getCard1Damage() + ", " + card2.getName() + "-> " + getCard2Damage() + "\n");

            if (card1.getType() == TYPE.SPELL || card2.getType() == TYPE.SPELL) {
                //does not happen with 2 monster cards
                addElementalDamage(card1, card2);
            }
            if (card1.getType() == TYPE.MONSTER || card2.getType() == TYPE.MONSTER) {
                //does not happen with 2 spell cards
                addMonsterSpecialities(card1, card2);
            }
            writeToLogfile("Final damage: " + card1.getName() + "-> " + getCard1Damage() + ", " + card2.getName() + "-> " + getCard2Damage() + "\n");
        }catch(IOException e){
            e.printStackTrace();
        }
        int strongerCard;

        if(getCard1Damage() > getCard2Damage()){
            strongerCard = 1;
        }else if(getCard2Damage() > getCard1Damage()){
            strongerCard = 2;
        }else {
            strongerCard = 3;
        }
        return strongerCard;
    }

    public void changeCardOwner(Card card, User newOwner, User oldOwner) throws SQLException {
        if(!oldOwner.getMyDeck().getMyCards().remove(card)){
            //remove returns false if card not in deck
            throw new IllegalArgumentException("Deck does not contain Card, unable to remove");
        }
        for(Card remCard : oldOwner.getMyStack().getMyCards()){
            if(Objects.equals(remCard.getCardID(), card.getCardID())){
                if(!oldOwner.getMyStack().getMyCards().remove(remCard)){
                    //remove returns false if card not in stack
                    throw new IllegalArgumentException("Stack does not contain Card, unable to remove");
                }
                break;
            }
        }
        newOwner.getMyDeck().addCard(card);
        newOwner.getMyStack().addCard(card);

        //db takes card + newOwner, updates
        getBattleDirectController().changeCardOwner(card, newOwner.getUserID());
    }

    public void writeToLogfile(String content) throws IOException {
        String fileName = "battle-log-" + getUser1().getUsername() + "_" + getUser2().getUsername() +".txt";
        PrintWriter writer = new PrintWriter(new FileWriter("C:\\MARLIS\\Fh_Technikum\\Semester 3\\Software\\MonsterTradingCardGame-Tiefengraber\\log\\" + fileName, true));
        writer.write(content);
        writer.close();
    }

    public void updatePlayerStats() throws SQLException {
        try {
            if (getUser1().getMyDeck().isDeckEmpty()) {
                //User1 lost
                getUser1().setScore(getUser1().getScore() - 5);
                getUser2().setScore(getUser2().getScore() + 3);
                writeToLogfile("User " + getUser2().getUsername() + " won the battle\n");
            } else if (getUser2().getMyDeck().isDeckEmpty()) {
                //User2 lost
                getUser2().setScore(getUser2().getScore() - 5);
                getUser1().setScore(getUser1().getScore() + 3);
                writeToLogfile("User " + getUser1().getUsername() + " won the battle\n");
            } else {
                writeToLogfile("The battle ended in a draw\n");
            }
            getUser1().setGamesPlayed(getUser1().getGamesPlayed() + 1);
            getUser2().setGamesPlayed(getUser2().getGamesPlayed() + 1);

            //update users in database
            getBattleDirectController().updatePlayerStats(getUser1(), getUser2());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void doBattle() throws SQLException{
        try {
            String fileName = "battle-log-" + getUser1().getUsername() + "_" + getUser2().getUsername() +".txt";
            File oldLog = new File(fileName);
            Files.deleteIfExists(oldLog.toPath());
            File myLog = new File("C:\\MARLIS\\Fh_Technikum\\Semester 3\\Software\\MonsterTradingCardGame-Tiefengraber\\log\\" + fileName);
            writeToLogfile("\nNEW BATTLE\n\n");
            writeToLogfile("User '" + getUser1().getUsername() + "' and User '" + getUser2().getUsername() + "' are battling\n");
            while (shouldBattleContinue()) {
                Card cardUser1; //this is compared
                Card cardUser2; //this is compared
                Card basisCard1; //this is chosen from deck
                Card basisCard2; //this is chosen from deck
                if(getRound()%10 == 0){
                    writeToLogfile("This is a mutation round!\n");
                    //each tenth round is a mutation round
                    basisCard1 = getRandomCardFromUser(getUser1());
                    basisCard2 = getRandomCardFromUser(getUser2());
                    cardUser1 = getMutationCardFromUser(getUser1(), basisCard1);
                    cardUser2 = getMutationCardFromUser(getUser2(), basisCard2);
                }else{
                    basisCard1 = getRandomCardFromUser(getUser1());
                    basisCard2 = getRandomCardFromUser(getUser2());
                    cardUser1 = basisCard1;
                    cardUser2 = basisCard2;
                }
                writeToLogfile("Round " + getRound() + ":\n");
                switch (whichCardStronger(cardUser1, cardUser2)) {
                    case 1 -> {
                        changeCardOwner(basisCard2, getUser1(), getUser2());
                        writeToLogfile("Card " + cardUser1.getName() + " of User " + getUser1().getUsername() + " won\n");
                        writeToLogfile("User " + getUser1().getUsername() + " gains Card " + basisCard2.getName() + "\n");
                    }
                    case 2 -> {
                        changeCardOwner(basisCard1, getUser2(), getUser1());
                        writeToLogfile("Card " + cardUser2.getName() + " of User " + getUser2().getUsername() + " won\n");
                        writeToLogfile("User " + getUser2().getUsername() + " gains Card " + basisCard1.getName() + "\n");

                    }
                    case 3 -> writeToLogfile("The round ended in a draw\n");
                    default -> throw new IllegalArgumentException("WhichCardStronger() returned illegal argument");
                }
                setRound(getRound() + 1);
                writeToLogfile("\n");
            }
            updatePlayerStats();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}
