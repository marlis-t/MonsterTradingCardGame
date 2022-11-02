package user;

import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
public class Demand {
    private String userID;
    private int minDamage;
    private ELEMENT element;
    private TYPE type;

    Demand(String userID, int minDamage, ELEMENT element, TYPE type) {
        setUserID(userID);
        setMinDamage(minDamage);
        setElement(element);
        setType(type);
    }
}
