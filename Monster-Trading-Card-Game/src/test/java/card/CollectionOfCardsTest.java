package card;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionOfCardsTest {
    @Test
    @DisplayName("Test: collection.addCard(null); NullPointerException thrown")
    public void testAddingNullToList_expectNullPointerException(){
        CollectionOfCards collection = new CollectionOfCards();

        Exception thrownException = assertThrows(NullPointerException.class, () -> {
            collection.addCard(null);
        });
        //System.out.println(thrownException.getMessage());
    }
    @Test
    @DisplayName("Test: collection.addCard(testCard); no Exception thrown")
    public void testAddingCardToList_expectNoException(){
        CollectionOfCards collection = new CollectionOfCards();
        Card testCard = new Card("NormalSpell", 0, "1", 0);

        assertDoesNotThrow(() -> {
            collection.addCard(testCard);
        });
    }

    @Test
    @DisplayName("Test: collection.removeCard(cardNotInList); IndexOutOfBoundsException thrown")
    public void testRemovingCardNotInList_expectIndexOutOfBoundsException(){
        CollectionOfCards collection = new CollectionOfCards();
        Card cardNotInList = new Card("NormalSpell", 0, "1", 0);

        Exception thrownException = assertThrows(IndexOutOfBoundsException.class, () -> {
            collection.removeCard(cardNotInList);
        });
        //System.out.println(thrownException.getMessage());
    }
    @Test
    @DisplayName("Test: collection.removeCard(cardInList); no Exception thrown")
    public void testRemovingCardInList_expectNoException(){
        CollectionOfCards collection = new CollectionOfCards();
        Card cardInList = new Card("NormalSpell", 0, "1", 0);
        collection.addCard(cardInList);

        assertDoesNotThrow(() -> {
            collection.removeCard(cardInList);
        });
    }


}
