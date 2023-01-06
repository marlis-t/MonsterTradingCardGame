package app.models;

import card.Enum.TYPE;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
public class TradingDeal {
    @JsonAlias({"tradeID"})
    private String tradeID;
    @JsonAlias({"userID"})
    private int userID;
    @JsonAlias({"cardToTradeID"})
    private String cardToTradeID;
    @JsonAlias({"minDamage"})
    private int minDamage;
    @JsonAlias({"type"})
    private TYPE type;

    public TradingDeal(String tradeID, int userID, String cardToTradeID, int minDamage, String type) {
        setTradeID(tradeID);
        setUserID(userID);
        setCardToTradeID(cardToTradeID);
        setMinDamage(minDamage);
        if(type.equalsIgnoreCase("Monster")){
            setType(TYPE.MONSTER);
        }else if(type.equalsIgnoreCase("Spell")){
            setType(TYPE.SPELL);
        }else{
            setType(null);
        }
    }
}
