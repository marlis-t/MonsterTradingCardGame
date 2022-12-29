package user;

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


}
