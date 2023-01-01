package card;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionOfCardsTest {
    private CollectionOfCards collection;

    @BeforeEach
    public void setup(){
        collection = new CollectionOfCards();
    }
    @Test
    @DisplayName("Test: collection.addCard(null); NullPointerException thrown")
    public void testAddingNullToList_expectNullPointerException(){
        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            collection.addCard(null);
        });
    }
    @Test
    @DisplayName("Test: collection.addCard(testCard); no Exception thrown")
    public void testAddingCardToList_expectNoException(){
        Card testCard = new Card("1", 0, "NormalSpell", 0, false);

        assertDoesNotThrow(() -> {
            collection.addCard(testCard);
        });
    }

    @Test
    @DisplayName("Test: collection.removeCard(cardNotInList); IndexOutOfBoundsException thrown")
    public void testRemovingCardNotInList_expectIndexOutOfBoundsException(){
        Card cardNotInList = new Card("1", 0, "NormalSpell", 0, false);

        Exception thrownException = assertThrows(IndexOutOfBoundsException.class, () -> {
            collection.removeCard(cardNotInList);
        });
    }
    @Test
    @DisplayName("Test: collection.removeCard(cardInList); no Exception thrown")
    public void testRemovingCardInList_expectNoException(){
        Card cardInList = new Card("1", 0, "NormalSpell", 0, false);

        collection.addCard(cardInList);

        assertDoesNotThrow(() -> {
            collection.removeCard(cardInList);
        });
    }


}
