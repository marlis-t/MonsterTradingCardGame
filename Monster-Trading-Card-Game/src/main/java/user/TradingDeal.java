package user;

import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
public class TradingDeal {
    private int offerCardID;
    private String offerCardName;
    private int offerCardDamage;
    private int userID;
    private int minDamage;
    private ELEMENT element;
    private TYPE type;

    public TradingDeal() {

    }
}
