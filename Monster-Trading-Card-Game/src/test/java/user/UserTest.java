package user;

import card.Card;
import card.Package;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    @Test
    @DisplayName("Test: ")
    public void test_expect(){

    }
    @Test
    @DisplayName("Test: buyPackage(pack); expect no Exception")
    public void testBuyValidPackage_expectNoException(){
        User user = new User("username", "user");
        Package pack = new Package();

        assertDoesNotThrow(() -> {
            user.buyPackage(pack);
        });
    }
    @Test
    @DisplayName("Test: buyPackage(null); expect IllegalArgumentException")
    public void testBuyInvalidPackage_expectIllegalArgumentException(){
        User user = new User("username", "user");

        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            user.buyPackage(null);
        });
    }
    @Test
    @DisplayName("Test: setUpTradingDeal(); expect no Exception")
    public void testSetUpTradingDealWithValidCard_expectNoException(){
        User user = new User("username", "user");
        Card card = new Card("NormalSpell", 20, 1, 1);
        user.getMyStack().addCard(card);

        assertDoesNotThrow(() -> {
            user.setUpTradingDeal(1, 10, null, null);
        });
    }
    @Test
    @DisplayName("Test: setUpTradingDeal(); expect no Exception")
    public void testSetUpTradingDealWithCardNotInStack_expectIllegalArgumentException(){
        User user = new User("username", "user");

        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            user.setUpTradingDeal(1, 10, null, null);
        });
    }

}
