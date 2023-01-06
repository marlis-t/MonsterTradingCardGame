package app.controllers;

import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.TradeDao;
import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import app.models.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import app.models.TradingDeal;
import app.models.UserModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class TradingController extends Controller{
    private TradeDao tradeDao;
    private CardDao cardDao;
    private DeckDao deckDao;

    public TradingController(TradeDao tradeDao, UserDao userDao, CardDao cardDao, DeckDao deckDao){
        super(userDao);
        setTradeDao(tradeDao);
        setCardDao(cardDao);
        setDeckDao(deckDao);
    }

    public Response getTradingDeal(String tradeID, String username){
        try {
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            TradingDeal trade = getTradeDao().read(tradeID);
            if(trade == null){
                return sendResponseWithType("null", "TradingDeal does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            String tradeDataJSON = getObjectMapper().writeValueAsString(trade);
            return sendResponse(tradeDataJSON, "null", HttpStatus.OK );
        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    public Response getAllTradingDeals(String authToken){
        try {
            if(!isAuthorized(authToken)){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            ArrayList<TradingDeal> trades = getTradeDao().readAll();
            if(trades.isEmpty()){
                return sendResponseWithType("No TradingDeals", "null", HttpStatus.NO_CONTENT, ContentType.TEXT);
            }
            String tradeDataJSON = getObjectMapper().writeValueAsString(trades);
            return sendResponse(tradeDataJSON, "null", HttpStatus.OK);

        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }
    public Response createTradingDeal(String body, String username){
        try{
            String[] split = body.split("\"");
            String tradeID = "";
            String cardToTradeID = "";
            int minDamage = 0;
            String type = "";
            //find params if set in body
            int length = (int) Arrays.stream(split).count();
            for(int i = 0; i < length; i++){
                if(Objects.equals(split[i], "Id")){
                    tradeID = split[i+2];
                }else if(Objects.equals(split[i], "CardToTrade")){
                    cardToTradeID = split[i+2];
                }else if(Objects.equals(split[i], "Type")){
                    type = split[i+2];
                }else if(Objects.equals(split[i], "MinimumDamage")){
                    minDamage = Integer.parseInt(split[i+2]);
                }
            }
            if(tradeID.equals("") || cardToTradeID.equals("")){
                return sendResponseWithType("null", "TradeID or CardToTradeID not set", HttpStatus.BAD_REQUEST, ContentType.TEXT);
            }
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            //check if user exists
            UserModel user = getUserDao().read(username);
            if(user == null){
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            TradingDeal trade = new TradingDeal(tradeID, user.getUserID(), cardToTradeID, minDamage, type);
            //check if card belongs to user
            ArrayList<Card> userCards = getCardDao().readAllCardsFromUser(user.getUserID());
            if(userCards.isEmpty()){
                return sendResponseWithType("null", "User has no Cards", HttpStatus.CONFLICT, ContentType.TEXT);
            }
            boolean isOwned = false;
            Card cardToTrade = null;
            for(Card tempCard : userCards){
                if (Objects.equals(tempCard.getCardID(), trade.getCardToTradeID())) {
                    isOwned = true;
                    cardToTrade = tempCard;
                    break;
                }
            }
            if(!isOwned){
                return sendResponseWithType("null", "Card does not belong to User", HttpStatus.FORBIDDEN, ContentType.TEXT);
            }
            //check if card is in deck
            ArrayList<Card> deckCards;
            deckCards = getDeckDao().readDeck(user.getUserID());
            if(!deckCards.isEmpty()){
                boolean isInDeck = false;
                for(Card temp : deckCards){
                    if(Objects.equals(temp.getCardID(), trade.getCardToTradeID())){
                        isInDeck = true;
                        break;
                    }
                }
                if(isInDeck){
                    return sendResponseWithType("null", "Card is in Deck", HttpStatus.FORBIDDEN, ContentType.TEXT);
                }
            }
            //check if trade id exists
            ArrayList<TradingDeal> trades = getTradeDao().readAll();
           if(!trades.isEmpty()){
               for(TradingDeal temp : trades){
                   if(Objects.equals(temp.getTradeID(), trade.getTradeID())){
                       return sendResponseWithType("null", "ID already exists", HttpStatus.CONFLICT, ContentType.TEXT);
                   }
               }
           }
            //change card status to paused and update
            getTradeDao().create(trade);
            cardToTrade.setPaused(true);
           getCardDao().update(cardToTrade);
           String tradeDataJson = getObjectMapper().writeValueAsString(trade);
           return sendResponse(tradeDataJson, "null", HttpStatus.CREATED);

        }catch(JsonProcessingException | SQLException e){
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    public Response carryOutTradingDeal(String tradeID, String body, String username){
        try{
            String[] split = body.split("\"");
            String offeredCardID = "";
            //find params if set in body
            int length = (int) Arrays.stream(split).count();
            for(int i = 0; i < length; i++) {
                if (Objects.equals(split[i], "Id")) {
                    offeredCardID = split[i + 2];
                }
            }
            if(offeredCardID.equals("")){
                return sendResponseWithType("null", "No Card offered", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            //check if offered Card exists
            Card offeredCard = getCardDao().read(offeredCardID);
            if(offeredCard == null){
                return sendResponseWithType("null", "Offered Card does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            //check if tradingDeal exists
            TradingDeal trade = getTradeDao().read(tradeID);
            if(trade == null){
                return sendResponseWithType("null", "TradingDeal does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            //check if authToken correct
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            //check if offered Card belongs to user
            UserModel user = getUserDao().read(username);
            if(user == null){
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            if(offeredCard.getUserID() != user.getUserID()){
                return sendResponseWithType("null", "Offered Card does not belong to User", HttpStatus.FORBIDDEN, ContentType.TEXT);
            }
            //check if trying to trade with self
            Card cardInTrade = getCardDao().read(trade.getCardToTradeID());
            if(cardInTrade == null){
                return sendResponseWithType("null", "Card in TradingDeal does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            if(cardInTrade.getUserID() == user.getUserID()){
                return sendResponseWithType("null", "Trying to trade with self", HttpStatus.FORBIDDEN, ContentType.TEXT);
            }
            //check if offeredCard is in Deck
            ArrayList<Card> deck = getDeckDao().readDeck(user.getUserID());
            if(!deck.isEmpty()){
                boolean inDeck = false;
                for(Card temp : deck){
                    if(Objects.equals(temp.getCardID(), offeredCardID)){
                        inDeck = true;
                        break;
                    }
                }
                if(inDeck){
                    return sendResponseWithType("null", "Offered Card in Deck", HttpStatus.FORBIDDEN, ContentType.TEXT);
                }
            }
            //check if offeredCard is paused
            if(offeredCard.isPaused()){
                return sendResponseWithType("null", "Offered Card in set in a TradingDeal", HttpStatus.FORBIDDEN, ContentType.TEXT);
            }
            //check if requirements met
            if(offeredCard.getDamage() < trade.getMinDamage() || offeredCard.getType() != trade.getType()){
                return sendResponseWithType("null", "Offered Card does not meet requirements", HttpStatus.FORBIDDEN, ContentType.TEXT);
            }
            //carry out trade -> update offeredCard UID and cardInTrade UID + paused
            //so beide für gleichen user
            int cardInTradeUser = cardInTrade.getUserID();
            int offeredCardUser = offeredCard.getUserID();
            offeredCard.setUserID(cardInTradeUser);
            cardInTrade.setUserID(offeredCardUser);
            cardInTrade.setPaused(false);
            getCardDao().update(offeredCard);
            getCardDao().update(cardInTrade);
            getTradeDao().delete(trade);
            return sendResponseWithType("Trade carried out", "null", HttpStatus.OK, ContentType.TEXT);

        }catch(SQLException e){
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }
    public Response deleteTradingDeal(String tradeID, String username){
        try {
            //check if trade with this id exists
            TradingDeal trade = getTradeDao().read(tradeID);
            if (trade == null) {
                return sendResponseWithType("null", "TradingDeal does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            //check if authtoken exists
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            //check if card in trade belongs to user
            UserModel user = getUserDao().read(username);
            if(user == null){
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            Card card = getCardDao().read(trade.getCardToTradeID());
            if(card == null){
                return sendResponseWithType("null", "Card in Trade does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            if(card.getUserID() != user.getUserID()){
                return sendResponseWithType("null", "Card in Trade does not belong to User", HttpStatus.FORBIDDEN, ContentType.TEXT);
            }
            //delete TradingDeal
            getTradeDao().delete(trade);
            String tradeDataJson = getObjectMapper().writeValueAsString(trade);
            return sendResponse("Deleted : " + tradeDataJson, "null", HttpStatus.OK);
        }catch(SQLException | JsonProcessingException e){
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }
}
