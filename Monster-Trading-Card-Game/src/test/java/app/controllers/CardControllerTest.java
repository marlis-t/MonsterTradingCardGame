package app.controllers;

import app.daos.CardDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import app.services.CardService;
import app.services.DatabaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardControllerTest {
    @Test
    @DisplayName("Test: cardController.getCardsFromUserID(-1); expect 'User #Id has no Cards'")
    public void testGetCardsFromNonexistentUser_expectWarningMessage(){
        CardController cardController = null;
        try {
            cardController = new CardController(new CardService(), new CardDao(new DatabaseService().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Response expectedResponse = new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"data\": User #" + -1 + " has no Cards, \"error\": null }"
        );

        Response realResponse = cardController.getCardsFromUserID(-1);

        Assertions.assertEquals(expectedResponse.getContent(), realResponse.getContent());
    }
    @Test
    @DisplayName("Test: cardController.getCardById(-1); expect 'No Card with this ID'")
    public void testGetCardWithNonexistentID_expectNotFoundResponse(){
        CardController cardController = null;
        try {
            cardController = new CardController(new CardService(), new CardDao(new DatabaseService().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Response expectedResponse =  new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"error\": \"No Card with this ID\", \"data\": null }"
        );

        Response realResponse = cardController.getCardById(-1);

        Assertions.assertEquals(expectedResponse.getContent(), realResponse.getContent());
    }
    @Test
    @DisplayName("Test: cardController.createCard(null); expect IllegalArgumentException")
    public void testCreateCardWithBodyNull_expectIllegalArgumentException(){
        CardController cardController = null;
        try {
            cardController = new CardController(new CardService(), new CardDao(new DatabaseService().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        CardController finalCardController = cardController;
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            finalCardController.createCard(null);
        });
    }
    @Test
    @DisplayName("Test: cardController.createCard(validString); expect no Exception")
    public void testCreateCardWithValidBody_expectNoException(){
        CardController cardController = null;
        try {
            cardController = new CardController(new CardService(), new CardDao(new DatabaseService().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String validBody = "{ \n" +
                "    \"cardID\": 4,\n" +
                "    \"userID\": 2,\n" +
                "    \"name\": \"NormalKnightMonster\",\n" +
                "    \"damage\": 20,\n" +
                "    \"element\": \"NORMAL\",\n" +
                "    \"type\": \"MONSTER\"\n" +
                "    }";

        CardController finalCardController = cardController;
        assertDoesNotThrow(() -> {
            finalCardController.createCard(validBody);
        });
    }
    @Test
    @DisplayName("Test: cardController.deleteCard(-1); expect 'No Card with this ID'")
    public void testDeleteCardWithNonexistentID_expectNotFoundResponse(){
        CardController cardController = null;
        try {
            cardController = new CardController(new CardService(), new CardDao(new DatabaseService().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Response expectedResponse =  new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"error\": \"No Card with this ID\", \"data\": null }"
        );

        Response realResponse = cardController.deleteCard(-1);

        Assertions.assertEquals(expectedResponse.getContent(), realResponse.getContent());
    }
    @Test
    @DisplayName("Test: cardController.updateCard(-1, validBody); expect 'No Card with this ID'")
    public void testUpdateCardWithNonexistentID_expectNotFoundResponse(){
        CardController cardController = null;
        try {
            cardController = new CardController(new CardService(), new CardDao(new DatabaseService().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Response expectedResponse =  new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"error\": \"No Card with this ID\", \"data\": null }"
        );
        String validBody = "{ \n" +
                "    \"cardID\": 4,\n" +
                "    \"userID\": 2,\n" +
                "    \"name\": \"NormalKnightMonster\",\n" +
                "    \"damage\": 20,\n" +
                "    \"element\": \"NORMAL\",\n" +
                "    \"type\": \"MONSTER\"\n" +
                "    }";

        Response realResponse = cardController.updateCard(-1, validBody);

        Assertions.assertEquals(expectedResponse.getContent(), realResponse.getContent());
    }
    @Test
    @DisplayName("Test: cardController.updateCard(-1, validBody); expect IllegalArgumentException")
    public void testUpdateCardWithBodyNull_expectIllegalArgumentException(){
        CardController cardController = null;
        try {
            cardController = new CardController(new CardService(), new CardDao(new DatabaseService().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        CardController finalCardController = cardController;
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            finalCardController.updateCard(1, null);
        });
    }
}
