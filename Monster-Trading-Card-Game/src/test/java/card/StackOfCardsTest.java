package card;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StackOfCardsTest {

    @Test
    @DisplayName("Test: stack.addCards(null); NullPointerException thrown")
    public void testAddingNullToList_expectNullPointerException(){
        StackOfCards stack = new StackOfCards();

        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            stack.addCards(null);
        });
        //System.out.println(thrownException.getMessage());
    }
    @Test
    @DisplayName("Test: stack.addCards(testCard); no Exception thrown")
    public void testAddingCardsToList_expectNoException(){
        StackOfCards stack = new StackOfCards();
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
