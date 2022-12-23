package app.controllers;

import app.daos.CardDao;
import app.daos.PackageDao;
import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import card.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import user.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class CardController extends Controller{
    //private CardService cardService;
    private CardDao cardDao;
    private UserDao userDao;
    private PackageDao packageDao;

    public CardController(CardDao cardDao, UserDao userDao, PackageDao packageDao) {
        setCardDao(cardDao);
        setUserDao(userDao);
        setPackageDao(packageDao);
    }

    //GET /cards *****
    public Response getCardsFromUser(String username) {
        try {
            User user = getUserDao().read(username);
            if(user == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            ArrayList<Card> cardData = getCardDao().readAllCardsFromUser(user.getUserID());
            if(cardData.size()==0){
                return sendResponse("null", "No Cards for this User", HttpStatus.NOT_FOUND);
            }
            String cardDataJSON = getObjectMapper().writeValueAsString(cardData);
            return sendResponse(cardDataJSON, "null", HttpStatus.OK);
        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //----
    /*
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

    // GET /cards/id not needed
    /*
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
    */
    // POST /packages
    public Response createPackage(String body) {
        try{
            String[] splitCards = body.split("\\{");
            ArrayList<Card> packCards = new ArrayList<Card>();
            for(String cardSplit : splitCards){
                String[] splitParams = body.split("\"");
                String ID = "";
                String name = "";
                int damage = 0;
                //find ID + name + damage if set in body
                int length = (int) Arrays.stream(splitParams).count();
                for(int i = 0; i < length; i++){
                    if(Objects.equals(splitParams[i], "Id")){
                        ID = splitParams[i+2];
                    }else if(Objects.equals(splitParams[i], "Name")){
                        name = splitParams[i+2];
                    }else if(Objects.equals(splitParams[i], "Damage")){
                        damage = (int) Float.parseFloat(splitParams[i+2]);
                    }
                }
                if(ID.equals("") || name.equals("") || damage == 0){
                    return sendResponse("null", "Invalid Information for Card declaration", HttpStatus.BAD_REQUEST);

                }
                Card card = new Card(ID, 0, name, damage, false);
                packCards.add(card);
            }
            if(packCards.size() < 5){
                return sendResponse("null", "Invalid Information for Card declaration", HttpStatus.BAD_REQUEST);
            }

            ArrayList<Card> createdCards = getPackageDao().create(packCards);
            String cardDataJSON = getObjectMapper().writeValueAsString(createdCards);

            return sendResponse(cardDataJSON, "null", HttpStatus.OK);

        }catch(JsonProcessingException | SQLException e){
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //POST /packages/transactions
    public Response acquirePackage(String username){
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            //check if enough coins
            User user = getUserDao().read(username);
            if(user.getCoins() < 5){
                return sendResponse("null", "Not enough coins", HttpStatus.BAD_REQUEST);
            }
            //get one package
            ArrayList<Card> packCards = getPackageDao().readPackage();
            if(packCards.size() < 5){
                return sendResponse("null", "No package found", HttpStatus.NOT_FOUND);
            }
            //change UID of Cards and put into Card table
            for(Card card : packCards){
                card.setUserID(user.getUserID());
                getCardDao().create(card);
            }
            //remove bought package
            getPackageDao().delete();
            //deduct user money
            user.setCoins(user.getCoins() - 5);
            getUserDao().update(user);

        }catch(SQLException e){
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return sendResponse("Bought package", "null", HttpStatus.OK);
    }
    public Boolean isAuthorized(String authToken) throws SQLException {
        ArrayList<String> auths;
        auths = getUserDao().readAuthToken();
        return auths.contains(authToken);
    }
    public Response sendResponse(String data, String error, HttpStatus status){
        return new Response(
                status,
                ContentType.JSON,
                "{ \"data\": \"" + data + "\", \"error\": " + error + " }"
        );
    }
}
