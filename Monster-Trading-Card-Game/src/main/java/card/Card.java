package card;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import card.Enum.ELEMENT;
import card.Enum.TYPE;

@Getter
@Setter(AccessLevel.PRIVATE)

public class Card {

    private int cardID;
    @Setter(AccessLevel.PUBLIC)
    private int UserID;
    private String name;
    private int damage;
    @Setter(AccessLevel.PUBLIC)
    private boolean paused;
    private ELEMENT element;
    private TYPE type;


    public Card(String name, int damage){
        setName(name);
        setDamage(damage);
        setPaused(false);
        setElement(name);
        setType(name);
        //get biggest ID from DB and +1 for ID
    }
    public Card(String name, int damage, int ID, int UserID, boolean paused){
        setName(name);
        setDamage(damage);
        setElement(name);
        setType(name);
        setCardID(ID);
        setUserID(UserID);
        setPaused(paused);
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
