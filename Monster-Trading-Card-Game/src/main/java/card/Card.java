package card;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)

public class Card {

    private String name;
    private int damage;
    @Setter(AccessLevel.PUBLIC)
    private boolean paused;
    private ELEMENT element;
    private TYPE type;


    Card(String name, int damage){
        setName(name);
        setDamage(damage);
        setPaused(false);
        setElement(name);
        setType(name);

    }

    private void setElement(String name){
        if(name.contains("Water")){
            element = ELEMENT.WATER;
        } else if(name.contains("Fire")){
            element = ELEMENT.FIRE;
        }else if(name.contains("Normal")){
            element = ELEMENT.NORMAL;
        }else{
            throw new IllegalArgumentException("Name does not contain element.");
        }
    }
    private void setType(String name){
        if(name.contains("Spell")){
            type = TYPE.SPELL;
        }else if(name.contains("Monster")){
            type = TYPE.MONSTER;
        }else{
            throw new IllegalArgumentException("Name does not contain type.");
        }
    }

    public void showCard(){
        System.out.println("Name: " + getName()  + "\n" + "Damage: " + getDamage() + "\n" );
    }

}
