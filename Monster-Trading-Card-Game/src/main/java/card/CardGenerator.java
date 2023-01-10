package card;

import app.models.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class CardGenerator {
    private ArrayList<String> Elements;
    private ArrayList<String> Types;
    private ArrayList<String> Specifications;
    private ArrayList<Integer> Damages;

    private Random randomizer = new Random();
    public CardGenerator(){
        //prepares generator with all possible values
        setElements(new ArrayList<String>());
        setTypes(new ArrayList<String>());
        setSpecifications(new ArrayList<String>());
        setDamages(new ArrayList<Integer>());

        getElements().add("Fire");
        getElements().add("Water");
        getElements().add("Normal");

        getTypes().add("Spell");
        getTypes().add("Monster");

        getSpecifications().add("Goblin");
        getSpecifications().add("Dragon");
        getSpecifications().add("Knight");
        getSpecifications().add("Elf");
        getSpecifications().add("Wizard");
        getSpecifications().add("Ork");
        getSpecifications().add("Kraken");
    }
    public Card generateCard(){
        int randDamage = 0;
        String cardName = null;
        try{
            String randElement = chooseElement(getRandomizer());
            String randType = chooseType(getRandomizer());
            String randSpec = "";
            //only monsters get specifications
            if(Objects.equals(randType, "Monster")){
                randSpec = chooseSpecification(getRandomizer());
            }
            randDamage = chooseDamage(getRandomizer());
            cardName = randElement + randSpec + randType;

        }catch(Exception e){
            e.printStackTrace();
        }
        //randomly generated card
        return new Card("0", 0, cardName, randDamage, false);
    }

    public String chooseElement(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        int randNr = randomizer.nextInt(getElements().size());
        return getElements().get(randNr);
    }
    public String chooseType(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        int randNr = randomizer.nextInt(getTypes().size());
        return getTypes().get(randNr);
    }
    public String chooseSpecification(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        int randNr = randomizer.nextInt(getSpecifications().size());
        return getSpecifications().get(randNr);
    }
    public int chooseDamage(Random randomizer){
        if(randomizer == null){
            throw new NullPointerException("Randomizer does not exist");
        }
        return randomizer.nextInt(10, 100);
    }
}
