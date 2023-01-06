package card;
import app.models.Card;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StackOfCardsTest {
    private StackOfCards stack;

    @BeforeEach
    public void setup(){
        stack = new StackOfCards();
    }

    @Test
    @DisplayName("Test: stack.addCards(null); NullPointerException thrown")
    public void testAddingNullToList_expectNullPointerException(){
        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            stack.addCards(null);
        });
    }
    @Test
    @DisplayName("Test: stack.addCards(testCard); no Exception thrown")
    public void testAddingCardsToList_expectNoException(){
        Card testCard1 = new Card("1", 0, "NormalSpell", 0, false);
        Card testCard2 = new Card("2", 0, "NormalSpell", 0, false);

        ArrayList<Card> cardList = new ArrayList<Card>();
        cardList.add(testCard1);
        cardList.add(testCard2);

        assertDoesNotThrow(() -> {
            stack.addCards(cardList);
        });
    }
    @Test
    @DisplayName("Test: best4Cards(); expect no Exception")
    public void testGettingBest4Cards_expectNoException(){
        Package pack = new Package();
        stack.addCards(pack.getMyCards());

        assertDoesNotThrow(() -> {
            stack.best4Cards();
        });
    }
    @Test
    @DisplayName("Test: stack.best4Cards(); IllegalStateException thrown")
    public void testGettingBest4CardsOfEmptyStack_expectIllegalStateException(){
        Exception thrownException = assertThrows(IllegalStateException.class, stack::best4Cards);
    }
}
