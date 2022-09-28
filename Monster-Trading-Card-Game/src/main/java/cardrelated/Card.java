package cardrelated;

import lombok.Getter;
import lombok.Setter;

public class Card {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int damage;
    @Getter
    @Setter
    private boolean paused;
    @Getter
    @Setter
    private String type;
    @Getter
    @Setter
    private String element;
    //public void setIsPaused(boolean isPaused) {this.paused = isPaused;}

    Card(String name, int damage, String type, String element){
        setName(name);
        setDamage(damage);
        setPaused(false);
        setType(type);
        setElement(element);
    }


}
