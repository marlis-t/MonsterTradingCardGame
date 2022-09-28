package cardrelated;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Package {
    @Getter
    @Setter
    private List<Card> packOfCards;

    Package(List<Card> packOfCards) {
        setPackOfCards(packOfCards);
    }
}
