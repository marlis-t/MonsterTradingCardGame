package tradingDeal;

import app.models.TradingDeal;
import card.Enum.TYPE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TradingDealTest {
    @Test
    @DisplayName("Test: TradingDeal(Monster); expect type == monster")
    public void testTradingDealMonster_expectMonster(){
        TradingDeal trade = new TradingDeal("1", 1, "1", 10, "monster");
        TYPE expectedType = TYPE.MONSTER;
        TYPE realType;

        realType = trade.getType();

        assertEquals(expectedType, realType);
    }
    @Test
    @DisplayName("Test: TradingDeal(Spell); expect type == spell")
    public void testTradingDealSpell_expectSpell(){
        TradingDeal trade = new TradingDeal("1", 1, "1", 10, "spell");
        TYPE expectedType = TYPE.SPELL;
        TYPE realType;

        realType = trade.getType();

        assertEquals(expectedType, realType);
    }

    @Test
    @DisplayName("Test: TradingDeal(invalidType); expect type == null")
    public void testTradingDealWithoutValidType_expectNull(){
        TradingDeal trade = new TradingDeal("1", 1, "1", 10, "invalid");
        TYPE realType;

        realType = trade.getType();

        assertNull(realType);
    }

}
