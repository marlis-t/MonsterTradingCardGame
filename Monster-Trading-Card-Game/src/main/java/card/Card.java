package card;

import lombok.Getter;

public class Card {
    @Getter
    private String name;
    @Getter
    private int damage;
    @Getter
    private boolean isPaused;
    @Getter
    private String type;
    @Getter
    private String element;
    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    Card(String name, int damage, String type, String element){
        this.name = name;
        this.damage = damage;
        this.isPaused = false;
        this.type = type;
        this.element = element;
    }


}
