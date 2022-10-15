package card;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StackTest {

    @Test
    @DisplayName("Test: stack.addCards(null); NullPointerException thrown")
    public void testAddingNullToList_expectNullPointerException(){
        Stack stack = new Stack();

        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            stack.addCards(null);
        });
    }
    @Test
    @DisplayName("Test: stack.addCards(testCard); no Exception thrown")
    public void testAddingCardsToList_expectNoException(){
        Stack stack = new Stack();
        Card testCard1 = new Card("NormalSpell", 0);
        Card testCard2 = new Card("NormalSpell", 0);
        ArrayList<Card> cardList = new ArrayList<Card>();
        cardList.add(testCard1);
        cardList.add(testCard2);

        assertDoesNotThrow(() -> {
            stack.addCards(cardList);
        });
    }

}
