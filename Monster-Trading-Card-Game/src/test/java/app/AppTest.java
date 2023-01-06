package app;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@Getter
@Setter
public class AppTest {
    private Connection connection;
    private App app;
    @BeforeEach
    public void setup(){
        connection = mock(Connection.class);
        app = new App(connection);
    }
    @Test
    @DisplayName("Test: ")
    public void test_expect(){

    }
    @Test
    @DisplayName("Test: parseUsernameFromPath(path); expect username")
    public void testParseUsernameFromPath_expectMatchingUsername(){
        String path = "http://localhost:7777/users/testname";
        String[] split = path.split("/");
        String expectedUsername = "testname";
        String realUsername;

        realUsername = getApp().parseUsernameFromPath(split);

        assertEquals(expectedUsername, realUsername);
    }
    @Test
    @DisplayName("Test: ParseUsernameFromPath(null); expect IllegalArgumentException")
    public void testParseUsernameFromPathNull_expectIllegalArgumentException(){
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            getApp().parseUsernameFromPath(null);
        });
    }
    @Test
    @DisplayName("Test: ParseUsernameFromPath(''); expect IllegalArgumentException")
    public void testParseUsernameFromPathEmpty_expectIllegalArgumentException(){
        String path = "";
        String[] split = path.split("/");
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            getApp().parseUsernameFromPath(split);
        });
    }

    @Test
    @DisplayName("Test: getUsernameFromToken(token); expect username")
    public void testGetUsernameFromToken_expectMatchingUsername(){
        String token = "testname-token";
        String expectedUsername = "testname";
        String realUsername;

        realUsername = getApp().getUsernameFromToken(token);

        assertEquals(realUsername, expectedUsername);
    }
    @Test
    @DisplayName("Test: getUsernameFromToken(null); expect IllegalArgumentException")
    public void testGetUsernameFromTokenNull_expectIllegalArgumentException(){
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            getApp().getUsernameFromToken(null);
        });
    }

    @Test
    @DisplayName("Test: doesAuthTokenMatchWithUser(user-token, user); == true")
    public void testDoesAuthTokenMatchWithUser_expectTrue(){
        String token = "testname-token";
        String user = "testname";
        boolean expectedResponse = true;
        boolean actualResponse;

        actualResponse = getApp().doesAuthTokenMatchWithUser(token, user);

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("Test: doesAuthTokenMatchWithUser(rand-token, user); == false")
    public void testDoesAuthTokenMatchWithUser_expectFalse(){
        String token = "rand-token";
        String user = "testname";
        boolean expectedResponse = true;
        boolean actualResponse;

        actualResponse = getApp().doesAuthTokenMatchWithUser(token, user);

        assertNotEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("Test: doesAuthTokenMatchWithUser(null, user); expect IllegalArgumentException")
    public void testDoesAuthTokenNullMatchWithUser_expectIllegalArgumentException(){
        String user = "testname";

        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            getApp().doesAuthTokenMatchWithUser(null, user);
        });
    }
}

