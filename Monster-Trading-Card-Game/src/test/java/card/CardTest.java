package card;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    @Test
    @DisplayName("Test: new Card(TestSpell, 0); IllegalArgumentException thrown")
    public void testNameWithoutElement_expectIllegalArgumentException(){
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            Card card = new Card("TestSpell", 0, 1);
        });
        //System.out.println(thrownException.getMessage());
    }
    @Test
    @DisplayName("Test: new Card(NormalTest, 0); IllegalArgumentException thrown")
    public void testNameWithoutType_expectIllegalArgumentException(){
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            Card card = new Card("NormalTest", 0, 1);
        });
        //System.out.println(thrownException.getMessage());
    }
    @Test
    @DisplayName("Test: new Card(NormalSpell, 0); no Exception thrown")
    public void testNameWithElementAndType_exceptNoException(){
        assertDoesNotThrow(() -> {
            Card card = new Card("NormalSpell",0, 1);
        });
    }
    @Test
    @DisplayName("card.isPaused(); == false")
    public void testBooleanPausedForNewCard_expectFalse(){
        Card card = new Card("NormalSpell", 0, 1);
        boolean expectedBoolean = false;

        boolean actualBoolean = card.isPaused();

        assertEquals(expectedBoolean, actualBoolean);
    }
}
