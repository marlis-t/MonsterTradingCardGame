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
        Card testCard1 = new Card("NormalSpell", 0,"1", 0);
        Card testCard2 = new Card("NormalSpell", 0, "2", 0);
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
        StackOfCards stack = new StackOfCards();
        Package pack = new Package();
        stack.addCards(pack.getMyCards());

        assertDoesNotThrow(() -> {
            stack.best4Cards();
        });
    }
    @Test
    @DisplayName("Test: best4Cards(); expect 2")
    public void testGettingBest4CardsOfSmallStack_expect2(){
        StackOfCards stack = new StackOfCards();
        Card card1 = new Card("NormalSpell", 20, "1", 0);
        Card card2 = new Card("NormalSpell", 25, "2", 0);
        Card card3 = new Card("NormalSpell", 30, "3", 0);
        stack.addCard(card1);
        stack.addCard(card2);
        stack.addCard(card3);
        ArrayList<Card> tempDeck;
        int expectedDeckSize = 2;
        int realDeckSize;

        stack.getMyCards().get(1).setPaused(true);
        tempDeck = stack.best4Cards();
        realDeckSize = tempDeck.size();

        assertEquals(expectedDeckSize, realDeckSize);
    }
    @Test
    @DisplayName("Test: stack.best4Cards(); IllegalStateException thrown")
    public void testGettingBest4CardsOfEmptyStack_expectIllegalStateException(){
        StackOfCards stack = new StackOfCards();

        Exception thrownException = assertThrows(IllegalStateException.class, stack::best4Cards);
        //System.out.println(thrownException.getMessage());
    }
}
