package card;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Random;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class CardGenerator {
    private ArrayList<String> Elements;
    private ArrayList<String> Types;
    private ArrayList<String> Specifications;
    private ArrayList<Integer> Damages;

    private Random randomizer = new Random();
    public CardGenerator(){
        setElements(new ArrayList<String>());
        setTypes(new ArrayList<String>());
        setSpecifications(new ArrayList<String>());
        setDamages(new ArrayList<Integer>());

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
        Specifications.add("Ork");
        Specifications.add("Kraken");

        Damages.add(20);
        Damages.add(25);
        Damages.add(30);
        Damages.add(35);
        Damages.add(40);
        Damages.add(45);
        Damages.add(50);
    }
    public Card generateCard(){
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
        return new Card("0", 0, cardName, randDamage, false);
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
