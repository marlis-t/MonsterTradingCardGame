package app.controllers;

import app.http.ContentType;
import app.http.HttpStatus;
import app.models.CardModel;
import app.server.Response;
import app.services.CardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CardController extends Controller{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private CardService cardService;

    public CardController(CardService cardservice) {
        setCardService(cardservice);
    }

    //GET /cards/Uid
    public Response getCardsFromUserID(int Uid) {
        try {
            List cardData = getCardService().getCardsFromUserID(Uid);
            String cityDataJSON = getObjectMapper().writeValueAsString(cardData);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + cityDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    // GET /card/id
    public Response getCardById(int id) {
        try{
            CardModel card = getCardService().getCardByID(id);
            if(card == null){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"No City with this ID\", \"data\": null }"
                );
            }
            String cityDataJSON = getObjectMapper().writeValueAsString(card);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + cityDataJSON + ", \"error\": null }"
            );
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    // POST /card
    public Response createCard(String body) {
        try{
            CardModel card = getObjectMapper().readValue(body, CardModel.class);
            getCardService().addCard(card);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + card + ", \"error\": null }"
            );

        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }
    // DELETE /cities/:id
    public Response deleteCard(int id) {
        CardModel card = getCardService().getCardByID(id);
        if(card == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No City with this ID\", \"data\": null }"
            );
        }
        String cardName = card.getName();
        getCardService().deleteCard(id);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + cardName + " deleted" + ", \"error\": null }"
        );
    }
}
