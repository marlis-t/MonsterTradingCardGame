package cardrelated;
import java.util.*;

public class Package {
    private List<Card> packOfCards;

    public List<Card> getPackOfCards() {
        return this.packOfCards;
    }
    Package(List<Card> packOfCards) {
        this.packOfCards = packOfCards;
    }
}
