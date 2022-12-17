package app.controllers;

import app.daos.CardDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.models.CardModel;
import app.server.Response;
import app.services.CardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class CardController extends Controller{
    private CardService cardService;
    private CardDao cardDao;

    public CardController(CardService cardservice, CardDao cardDao) {
        setCardService(cardservice);
        setCardDao(cardDao);
    }

    //GET /cards/Uid
    public Response getCardsFromUserID(int Uid) {
        try {
            //what if cardData empty? JsonE thrown
            ArrayList<CardModel> cardData = getCardService().getCardsFromUserID(Uid);
            if(cardData.size()==0){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"error\": User #\" + Uid + \" has no Cards , \"data\": null}"
                );
            }
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
    public Response getAllOfferedCards(int Uid){
        try {
            ArrayList<CardModel> cardData = getCardService().getAllOfferedCards(Uid);
            if(cardData.size()==0){
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{}"
                );
            }
            String cardDataJSON = getObjectMapper().writeValueAsString(cardData);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + cardDataJSON + ", \"error\": null }"
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
                        "{ \"error\": \"No Card with this ID\", \"data\": null }"
                );
            }
            String cardDataJSON = getObjectMapper().writeValueAsString(card);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + cardDataJSON + ", \"error\": null }"
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
            //get highest Card ID
            getCardService().addCard(card);
            return new Response(
                    HttpStatus.CREATED,
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
    public Response createPackage(String body){
        try{
            ArrayList<CardModel> cards = new ArrayList<CardModel>();
            //this is wrong
            getCardService().addCard(getObjectMapper().readValue(body, CardModel.class));

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{\"data\": " + cards + ", \"error\": null }"
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
                    "{ \"error\": \"No Card with this ID\", \"data\": null }"
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
    //PUT /card/id
    public Response updateCard(int id, String body){
        CardModel oldCard = getCardService().getCardByID(id);
        if(oldCard == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No Card with this ID\", \"data\": null }"
            );
        }
        try{
            CardModel card = getObjectMapper().readValue(body, CardModel.class);
            getCardService().updateCard(id, card);
            return new Response(
                    HttpStatus.CREATED,
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
}
