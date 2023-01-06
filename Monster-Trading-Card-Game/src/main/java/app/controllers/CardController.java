package app.controllers;

import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.PackageDao;
import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import app.models.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import app.models.UserModel;

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
    private PackageDao packageDao;
    private DeckDao deckDao;

    public CardController(CardDao cardDao, UserDao userDao, PackageDao packageDao, DeckDao deckDao) {
        super(userDao);
        setCardDao(cardDao);
        setPackageDao(packageDao);
        setDeckDao(deckDao);
    }

    //GET /cards *****
    public Response getCardsFromUser(String username) {
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            UserModel user = getUserDao().read(username);
            if(user == null){
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            ArrayList<Card> cardData = getCardDao().readAllCardsFromUser(user.getUserID());
            if(cardData.size()==0){
                return sendResponseWithType("No Cards for this User", "null", HttpStatus.NO_CONTENT, ContentType.TEXT);
            }
            String cardDataJSON = getObjectMapper().writeValueAsString(cardData);
            return sendResponse(cardDataJSON, "null", HttpStatus.OK);
        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    //GET /decks
    public Response getDeckFromUser(String username, Boolean plain){
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            UserModel user = getUserDao().read(username);
            if(user == null){
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            ArrayList<Card> deck = getDeckDao().readDeck(user.getUserID());
            if(deck.size() == 0){
                return sendResponseWithType("No Cards in the Deck", "null", HttpStatus.NO_CONTENT, ContentType.TEXT);
            }
            String deckDataJson;
            if(plain){
                deckDataJson = plainCards(deck);
                return sendResponseWithType(deckDataJson, "null", HttpStatus.OK, ContentType.TEXT);
            }else{
                deckDataJson = getObjectMapper().writeValueAsString(deck);
                return sendResponse(deckDataJson, "null", HttpStatus.OK);
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

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
                    }else if(Objects.equals(splitParams[i], "Name")){
                        name = splitParams[i+2];
                    }else if(Objects.equals(splitParams[i], "Damage")){
                        damage = (int) Float.parseFloat(splitParams[i+2]);
                    }
                }
                if(ID.equals("") || name.equals("") || damage == 0){
                    return sendResponseWithType("null", "Invalid Information for Card declaration", HttpStatus.BAD_REQUEST, ContentType.TEXT);
                }
                Card card = new Card(ID, 0, name, damage, false);
                packCards.add(card);
                foundCards++;
                if(foundCards == 5){
                    break;
                }
            }
            if(packCards.size() < 5){
                return sendResponseWithType("null", "No full package created", HttpStatus.CONFLICT, ContentType.TEXT);
            }
            //check if id already exists
            ArrayList<Card> allCards;
            allCards = getCardDao().readAll();
            if(allCards != null){
                for(Card temp1 : allCards){
                    for(Card temp2 : packCards){
                        if(temp1.getCardID().equals(temp2.getCardID())){
                            return sendResponseWithType("null", "ID already exists", HttpStatus.CONFLICT, ContentType.TEXT);
                        }
                    }
                }
            }
            ArrayList<Card> createdCards = getPackageDao().create(packCards);
            return sendResponseWithType("Package created", "null", HttpStatus.CREATED, ContentType.TEXT);

        }catch(SQLException | IllegalArgumentException e){
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    //POST /packages/transactions
    public Response acquirePackage(String username){
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            //check if enough coins
            UserModel user = getUserDao().read(username);
            if(user == null){
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            if(user.getCoins() < 5){
                return sendResponseWithType("null", "Not enough coins", HttpStatus.BAD_REQUEST, ContentType.TEXT);
            }
            //get one package + remove it
            ArrayList<Card> packCards = getPackageDao().readPackage();
            if(packCards.isEmpty()){
                return sendResponseWithType("null", "No package found", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            //remove bought package
            //getPackageDao().delete();

            //change UID of Cards and put into Card table
            for(Card card : packCards){
                card.setUserID(user.getUserID());
                getCardDao().create(card);
            }
            //deduct user money
            user.setCoins(user.getCoins() - 5);
            getUserDao().update(user);
            String packDataJson = getObjectMapper().writeValueAsString(packCards);
            return sendResponse(packDataJson, "null", HttpStatus.OK);

        }catch(SQLException | JsonProcessingException e){
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }
    //POST /decks
    public Response assembleDeck(String body, String username){
        try {
            //check if Token ok
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
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
            UserModel user;
            //get user
            user = getUserDao().read(username);
            if(user == null){
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            //check if Cards belong to right user
            ArrayList<Card>chosenCards = new ArrayList<Card>();
            for(String id: cardIDs){
                System.out.println("find " + id);
                Card tempCard = getCardDao().read(id);
                if(tempCard == null){
                    return sendResponseWithType("null", "Card does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
                }else if(tempCard.getUserID() != user.getUserID()){
                    return sendResponseWithType("null", "Chosen Card does not belong to User", HttpStatus.FORBIDDEN, ContentType.TEXT);
                }
                if(tempCard.isPaused()){
                    return sendResponseWithType("null", "Card cannot be used in Deck", HttpStatus.FORBIDDEN, ContentType.TEXT);
                }
                chosenCards.add(tempCard);
            }
            //check if enough valid Cards found
            if(chosenCards.size() < 4){
                return sendResponseWithType("null", "Not enough Cards chosen for Deck", HttpStatus.BAD_REQUEST, ContentType.TEXT);
            }
            //delete old deck if exists
            getDeckDao().delete(user.getUserID());
            //create new deck
            ArrayList<Card> newDeck = getDeckDao().create(chosenCards);
            return sendResponseWithType("Deck successfully configured", "null", HttpStatus.OK, ContentType.TEXT);

        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
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
}
