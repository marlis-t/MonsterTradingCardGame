package card;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    @Test
    @DisplayName("card.isPaused(); == false")
    public void testBooleanPausedForNewObject_expectFalse(){
        Card card = new Card("NormalSpell", 0);
        boolean expectedBoolean = false;

        boolean actualBoolean = card.isPaused();

        assertEquals(expectedBoolean, actualBoolean);
    }
}
