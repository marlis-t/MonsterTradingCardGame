package app.controllers;

import app.daos.CardDao;
import app.daos.DeckDao;
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
import java.util.regex.Pattern;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class CardController extends Controller{
    //private CardService cardService;
    private CardDao cardDao;
    private UserDao userDao;
    private PackageDao packageDao;
    private DeckDao deckDao;

    public CardController(CardDao cardDao, UserDao userDao, PackageDao packageDao, DeckDao deckDao) {
        setCardDao(cardDao);
        setUserDao(userDao);
        setPackageDao(packageDao);
        setDeckDao(deckDao);
    }

    //GET /cards *****
    public Response getCardsFromUser(String username) {
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
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

    //GET /decks
    public Response getDeckFromUser(String username, Boolean plain){
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            User user = getUserDao().read(username);
            if(user == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            ArrayList<Card> deck = getDeckDao().readDeck(user.getUserID());
            String deckDataJson;
            if(plain){
                deckDataJson = plainCards(deck);
            }else{
                deckDataJson = getObjectMapper().writeValueAsString(deck);
            }
            return sendResponse(deckDataJson, "null", HttpStatus.OK);
        } catch (SQLException | JsonProcessingException e) {
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
    */
    // POST /packages
    public Response createPackage(String body) {
        try{
            //separate cards
            String[] splitByCards = body.split(Pattern.quote("}"));
            ArrayList<Card> packCards = new ArrayList<Card>();
            int foundCards = 0;
            for(String aSplitCard : splitByCards){
                String[] splitParams = aSplitCard.split("\"");
                String ID = "";
                String name = "";
                int damage = 0;
                //find ID + name + damage if set in body
                int length = (int) Arrays.stream(splitParams).count();
                for(int i = 0; i < length; i++){
                    if(Objects.equals(splitParams[i], "Id")){
                        ID = splitParams[i+2];
                        System.out.println(ID + " id");
                    }else if(Objects.equals(splitParams[i], "Name")){
                        name = splitParams[i+2];
                        System.out.println(name + " name");
                    }else if(Objects.equals(splitParams[i], "Damage")){
                        damage = (int) Float.parseFloat(splitParams[i+2]);
                        System.out.println(damage + " damage");
                    }
                }
                System.out.println("done with params for 1 card");
                if(ID.equals("") || name.equals("") || damage == 0){
                    return sendResponse("null", "Invalid Information for Card declaration", HttpStatus.BAD_REQUEST);
                }
                Card card = new Card(ID, 0, name, damage, false);
                System.out.println("created card");
                packCards.add(card);
                foundCards++;
                if(foundCards == 5){
                    break;
                }
            }
            if(packCards.size() < 5){
                return sendResponse("null", "No full package created", HttpStatus.BAD_REQUEST);
            }
            ArrayList<Card> createdCards = getPackageDao().create(packCards);
            String cardDataJSON = getObjectMapper().writeValueAsString(createdCards);

            return sendResponse(cardDataJSON, "null", HttpStatus.CREATED);

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
            if(user == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            if(user.getCoins() < 5){
                return sendResponse("null", "Not enough coins", HttpStatus.BAD_REQUEST);
            }
            //get one package
            ArrayList<Card> packCards = getPackageDao().readPackage();
            if(packCards.isEmpty()){
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
    //POST /decks
    public Response assembleDeck(String body, String username){
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            String[] splitByCardID = body.split("\"");
            ArrayList<String> cardIDs = new ArrayList<String>();
            //get all IDs from body
            int length = (int) Arrays.stream(splitByCardID).count();
            for(int i = 0; i < length; i++){
                String ID;
                //find ID if set in body
                if (Objects.equals(splitByCardID[i], "Id")) {
                    ID = splitByCardID[i + 2];
                    cardIDs.add(ID);
                }
            }
            User user;
            //get user
            user = getUserDao().read(username);
            if(user == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            //check if Cards belong to right user
            ArrayList<Card>chosenCards = new ArrayList<Card>();
            for(String id: cardIDs){
                Card tempCard = getCardDao().read(id);
                if(tempCard == null){
                    return sendResponse("null", "Card does not exist", HttpStatus.NOT_FOUND);
                }else if(tempCard.getUserID() != user.getUserID()){
                    return sendResponse("null", "Chosen Card does not belong to User", HttpStatus.UNAUTHORIZED);
                }
                chosenCards.add(tempCard);
            }
            //check if enough valid Cards found
            if(chosenCards.size() < 5){
                return sendResponse("null", "Not enough Cards chosen for Deck", HttpStatus.BAD_REQUEST);
            }
            //delete old deck if exists
            getDeckDao().delete(user.getUserID());
            //create new deck
            ArrayList<Card> newDeck = getDeckDao().create(chosenCards);
            String deckDataJson = getObjectMapper().writeValueAsString(newDeck);
            return sendResponse(deckDataJson, "null", HttpStatus.CREATED);

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Boolean isAuthorized(String authToken) throws SQLException {
        ArrayList<String> auths;
        auths = getUserDao().readAuthToken();
        return auths.contains(authToken);
    }
    public String plainCards(ArrayList<Card> deck){
        StringBuilder plainCards = new StringBuilder();
        for(Card card : deck){
            plainCards.append("ID: ").append(card.getCardID()).append("\n");
            plainCards.append("UserID: ").append(card.getUserID()).append("\n");
            plainCards.append("Name: ").append(card.getName()).append("\n");
            plainCards.append("Damage: ").append(card.getDamage()).append(",\n");
        }
        return plainCards.toString();
    }
    public Response sendResponse(String data, String error, HttpStatus status){
        return new Response(
                status,
                ContentType.JSON,
                "{ \"data\": \"" + data + "\", \"error\": " + error + " }"
        );
    }
}
