package card;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Random;

@Getter(AccessLevel.PRIVATE)
public class CardGenerator {
    private ArrayList<String> Elements;
    private ArrayList<String> Types;
    private ArrayList<String> Specifications;
    private ArrayList<Integer> Damages;
    public CardGenerator(){
        Elements.add("Fire");
        Elements.add("Water");
        Elements.add("Normal");

        Types.add("Spell");
        Types.add("Monster");

        Specifications.add("Goblin");
        Specifications.add("Dragon");
        Specifications.add("Knight");
        Specifications.add("Elf");
        Specifications.add("Wizard");
        Specifications.add("Ogre");

        Damages.add(10);
        Damages.add(15);
        Damages.add(20);
        Damages.add(25);
        Damages.add(30);
    }
    public Card generateCard(){
        Random randomizer = new Random();
        int randDamage = 0;
        String cardName = null;
        try{
            String randElement = chooseElement(randomizer);
            String randType = chooseType(randomizer);
            String randSpec;
            if(randType == "Monster"){
                randSpec = chooseSpecification(randomizer);
            }else{
                randSpec = "Magic";
            }
            randDamage = chooseDamage(randomizer);
            cardName = randElement + randSpec + randType;

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        Card generatedCard = new Card(cardName, randDamage);
        return generatedCard;
    }

    public String chooseElement(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        int randNr = randomizer.nextInt(Elements.size());
        return Elements.get(randNr);
    }
    public String chooseType(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        int randNr = randomizer.nextInt(Types.size());
        return Types.get(randNr);
    }
    public String chooseSpecification(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        int randNr = randomizer.nextInt(Specifications.size());
        return Specifications.get(randNr);
    }
    public int chooseDamage(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        int randNr = randomizer.nextInt(Damages.size());
        return Damages.get(randNr);
    }
}
