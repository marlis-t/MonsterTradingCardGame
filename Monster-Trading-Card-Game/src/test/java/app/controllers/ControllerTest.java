package app.controllers;

import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerTest {
    private Controller controller;
    private UserDao userDao;
    @BeforeEach
    public void setup(){
        userDao = mock(UserDao.class);
        controller = new Controller(userDao);
    }
    @Test
    @DisplayName("Test: isAuthorized(validToken); expect true")
    public void testIsAuthorizedValidToken_expectTrue() throws SQLException {
        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        when(userDao.readAuthToken()).thenReturn(tokens);
        boolean expected = true;
        boolean real;

        real = controller.isAuthorized("validToken");

        assertEquals(expected, real);
    }
    @Test
    @DisplayName("Test: isAuthorized(invalidToken); expect false")
    public void testIsAuthorizedInvalidToken_expectFalse() throws SQLException {
        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        when(userDao.readAuthToken()).thenReturn(tokens);
        boolean expected = false;
        boolean real;

        real = controller.isAuthorized(null);

        assertEquals(expected, real);
    }
    @Test
    @DisplayName("Test: SendResponseWithType(); expect correct parameters")
    public void testSendResponse_expectCorrectParams() {
        final Response[] realResponse = new Response[1];
        HttpStatus expectedStatus = HttpStatus.OK;
        ContentType expectedContentType = ContentType.TEXT;
        String expectedContent = "content, error: null \n";

        assertDoesNotThrow(() -> {
            realResponse[0] = controller.sendResponseWithType("content", "null", HttpStatus.OK, ContentType.TEXT);
        });
        assertEquals(expectedStatus.getCode(), realResponse[0].getStatusCode());
        assertEquals(expectedContentType.getType(), realResponse[0].getContentType());
        assertEquals(expectedContent, realResponse[0].getContent());
    }
    @Test
    @DisplayName("Test: SendResponseWithType(null); expect IllegalArgumentException")
    public void testSendResponseNull_expectIllegalArgumentException() {
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            controller.sendResponseWithType("content", "null", null, null);
        });
    }


}
