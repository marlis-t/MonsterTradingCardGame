package app.controllers;

import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.TradeDao;
import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import card.Card;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import tradingDeal.TradingDeal;
import user.User;

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
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            TradingDeal trade = getTradeDao().read(tradeID);
            if(trade == null){
                return sendResponse("null", "TradingDeal does not exist", HttpStatus.NOT_FOUND);
            }
            String tradeDataJSON = getObjectMapper().writeValueAsString(trade);
            return sendResponse(tradeDataJSON, "null", HttpStatus.OK );
        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getAllTradingDeals(String authToken){
        try {
            if(!isAuthorized(authToken)){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            ArrayList<TradingDeal> trades = getTradeDao().readAll();
            if(trades.isEmpty()){
                return sendResponse("No TradingDeals", "null", HttpStatus.NO_CONTENT);
            }
            String tradeDataJSON = getObjectMapper().writeValueAsString(trades);
            return sendResponse(tradeDataJSON, "null", HttpStatus.OK);

        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
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
                return sendResponse("null", "TradeID or CardToTradeID not set", HttpStatus.BAD_REQUEST);
            }
            System.out.println("found all info");
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            //check if user exists
            User user = getUserDao().read(username);
            if(user == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            TradingDeal trade = new TradingDeal(tradeID, user.getUserID(), cardToTradeID, minDamage, type);
            //check if card belongs to user
            ArrayList<Card> userCards = getCardDao().readAllCardsFromUser(user.getUserID());
            if(userCards.isEmpty()){
                return sendResponse("null", "User has no Cards", HttpStatus.CONFLICT);
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
                return sendResponse("null", "Card does not belong to User", HttpStatus.FORBIDDEN);
            }
            System.out.println("card belongs to user");
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
                    return sendResponse("null", "Card is in Deck", HttpStatus.FORBIDDEN);
                }
            }
            System.out.println("card not in deck");
            //check if trade id exists
            ArrayList<TradingDeal> trades = getTradeDao().readAll();
           if(!trades.isEmpty()){
               for(TradingDeal temp : trades){
                   if(Objects.equals(temp.getTradeID(), trade.getTradeID())){
                       return sendResponse("null", "ID already exists", HttpStatus.CONFLICT);
                   }
               }
           }
            System.out.println("trade id does not exist");
            //change card status to paused and update
            getTradeDao().create(trade);
            cardToTrade.setPaused(true);
           getCardDao().update(cardToTrade);
           String tradeDataJson = getObjectMapper().writeValueAsString(trade);
           return sendResponse(tradeDataJson, "null", HttpStatus.CREATED);

        }catch(JsonProcessingException | SQLException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
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
                return sendResponse("null", "No Card offered", HttpStatus.NOT_FOUND);
            }
            //check if offered Card exists
            Card offeredCard = getCardDao().read(offeredCardID);
            if(offeredCard == null){
                return sendResponse("null", "Offered Card does not exist", HttpStatus.NOT_FOUND);
            }
            //check if tradingDeal exists
            TradingDeal trade = getTradeDao().read(tradeID);
            if(trade == null){
                return sendResponse("null", "TradingDeal does not exist", HttpStatus.NOT_FOUND);
            }
            //check if authToken correct
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            //check if offered Card belongs to user
            User user = getUserDao().read(username);
            if(user == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            if(offeredCard.getUserID() != user.getUserID()){
                return sendResponse("null", "Offered Card does not belong to User", HttpStatus.FORBIDDEN);
            }
            //check if trying to trade with self
            Card cardInTrade = getCardDao().read(trade.getCardToTradeID());
            if(cardInTrade == null){
                return sendResponse("null", "Card in TradingDeal does not exist", HttpStatus.NOT_FOUND);
            }
            if(cardInTrade.getUserID() == user.getUserID()){
                return sendResponse("null", "Trying to trade with self", HttpStatus.FORBIDDEN);
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
                    return sendResponse("null", "Offered Card in Deck", HttpStatus.FORBIDDEN);
                }
            }
            //check if offeredCard is paused
            if(offeredCard.isPaused()){
                return sendResponse("null", "Offered Card in set in a TradingDeal", HttpStatus.FORBIDDEN);
            }
            //check if requirements met
            if(offeredCard.getDamage() < trade.getMinDamage() || offeredCard.getType() != trade.getType()){
                return sendResponse("null", "Offered Card does not meet requirements", HttpStatus.FORBIDDEN);
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
            return sendResponse("Trade carried out", "null", HttpStatus.OK);

        }catch(SQLException e){
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public Response deleteTradingDeal(String tradeID, String username){
        try {
            //check if trade with this id exists
            TradingDeal trade = getTradeDao().read(tradeID);
            if (trade == null) {
                return sendResponse("null", "TradingDeal does not exist", HttpStatus.NOT_FOUND);
            }
            //check if authtoken exists
            if(!isAuthorized(username + "-mtcgToken")){
                return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
            }
            //check if card in trade belongs to user
            User user = getUserDao().read(username);
            if(user == null){
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            Card card = getCardDao().read(trade.getCardToTradeID());
            if(card == null){
                return sendResponse("null", "Card in Trade does not exist", HttpStatus.NOT_FOUND);
            }
            if(card.getUserID() != user.getUserID()){
                return sendResponse("null", "Card in Trade does not belong to User", HttpStatus.FORBIDDEN);
            }
            //delete TradingDeal
            getTradeDao().delete(trade);
            String tradeDataJson = getObjectMapper().writeValueAsString(trade);
            return sendResponse("Deleted : " + tradeDataJson, "null", HttpStatus.OK);
        }catch(SQLException | JsonProcessingException e){
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
