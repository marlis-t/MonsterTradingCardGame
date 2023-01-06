package app.models;

import card.Enum.ELEMENT;
import card.Enum.TYPE;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Card{
    @JsonAlias({"cardID"})
    private String cardID;
    @JsonAlias({"userID"})
    private int userID;
    @JsonAlias({"name"})
    private String name;
    @JsonAlias({"damage"})
    private int damage;
    @JsonAlias({"paused"})
    private boolean paused;
    @JsonAlias({"element"})
    private ELEMENT element;
    @JsonAlias({"type"})
    private TYPE type;

    public Card(String cardID, int userID, String name, int damage, Boolean paused){
        setName(name);
        setDamage(damage);
        setPaused(paused);
        setElement(readElement(name));
        setType(readType(name));
        setUserID(userID);
        setCardID(cardID);
    }
    public Card(){}

    private ELEMENT readElement(String name){
        if(name.contains("Water")){
            return ELEMENT.WATER;
        } else if(name.contains("Fire")){
            return ELEMENT.FIRE;
        }else if(name.contains("Normal")){
            return ELEMENT.NORMAL;
        }else{
            throw new IllegalArgumentException("Name does not contain element.");
        }
    }
    private TYPE readType(String name){
        if(name.contains("Spell")){
            return TYPE.SPELL;
        }else{
            return TYPE.MONSTER;
        }
    }

    public String showCard(){
        return "{ \"ID\": \"" + getCardID() + "\"," +
                " \"UserID\": \"" + getUserID() + "\", " +
                " \"Name\": \"" + getName() + "\"," +
                " \"Damage\": \"" + getDamage() + "\" }";
    }



}
