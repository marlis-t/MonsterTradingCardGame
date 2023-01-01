package user;

import card.Package;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;

    @BeforeEach
    public void setup(){
        user = new User("username", "user");
    }
    @Test
    @DisplayName("Test: ")
    public void test_expect(){

    }
    @Test
    @DisplayName("Test: buyPackage(pack); expect no Exception")
    public void testBuyValidPackage_expectNoException(){
        Package pack = new Package();

        assertDoesNotThrow(() -> {
            user.buyPackage(pack);
        });
    }
    @Test
    @DisplayName("Test: buyPackage(null); expect IllegalArgumentException")
    public void testBuyInvalidPackage_expectIllegalArgumentException(){
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            user.buyPackage(null);
        });
    }

    @Test
    @DisplayName("Test: assembleDeck(); expect Deck of 4 Cards")
    public void testAssembleDeck_expectFullDeck(){
        Package pack = new Package();
        user.buyPackage(pack);
        int expectedDeckSize = 4;
        int realDeckSize;

        user.assembleDeck();
        realDeckSize = user.getMyDeck().getMyCards().size();

        assertEquals(expectedDeckSize, realDeckSize);
    }

}
