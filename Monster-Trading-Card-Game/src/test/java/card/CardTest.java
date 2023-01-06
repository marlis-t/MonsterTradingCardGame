package card;
import app.models.Card;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    @Test
    @DisplayName("Test: new Card(TestSpell, 0); IllegalArgumentException thrown")
    public void testNameWithoutElement_expectIllegalArgumentException(){
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            Card card = new Card("1", 0, "TestSpell", 0, false);
        });
    }
    @Test
    @DisplayName("Test: new Card(NormalSpell, 0); no Exception thrown")
    public void testNameWithElementAndType_expectNoException(){
        assertDoesNotThrow(() -> {
            Card card = new Card("1", 0, "NormalSpell", 0, false);

        });
    }
}
