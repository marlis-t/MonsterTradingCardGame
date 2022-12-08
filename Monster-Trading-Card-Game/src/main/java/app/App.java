package app;

import app.controllers.CardController;
import app.controllers.TradingController;
import app.controllers.UserController;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Request;
import app.server.Response;
import app.server.ServerApp;
import app.services.CardService;
import app.services.TradingService;
import app.services.UserService;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.Arrays;

public class App implements ServerApp {
    @Setter(AccessLevel.PRIVATE)
    private CardController cardController;
    @Setter(AccessLevel.PRIVATE)
    private UserController userController;
    @Setter(AccessLevel.PRIVATE)
    private TradingController tradingController;

    public App() {
        setCardController(new CardController(new CardService()));
        setUserController(new UserController(new UserService()));
        setTradingController(new TradingController(new TradingService()));
    }

    public Response handleRequest(Request request) {
        System.out.println(request.getPathname());
        System.out.println(request.getMethod());
        switch (request.getMethod()) {
            case GET -> {
                String[] split = request.getPathname().split("/");
                //getCardsByUID
                if (request.getPathname().contains("/cards/users/")) {
                    return this.cardController.getCardsFromUserID(parseId(split));
                }//getAllOfferedCards
                else if (request.getPathname().contains("/tradings/")) {
                    return this.tradingController.getAllTradingDeals(parseId(split));
                }//getAllUsersExceptSelf
                else if (request.getPathname().contains("/users/all/")) {//
                    return this.userController.getAllUsersExceptSelf(parseId(split));
                }//getCardByID
                else if (request.getPathname().contains("/cards/IDs/")) {
                    return this.cardController.getCardById(parseId(split));
                }//getUserByID
                else if (request.getPathname().contains("/users/IDs/")) {
                    return this.userController.getUserById(parseId(split));
                }//getDemandToOffer
            }
            case POST -> {
                //createCard
                if (request.getPathname().equals("/cards")) {
                    return this.cardController.createCard(request.getBody());
                }//createUser
                else if (request.getPathname().equals("/users")) {
                    return this.userController.createUser(request.getBody());
                } else if (request.getPathname().equals("/users/login")) {//
                    return this.userController.getUserByCredentials(request.getBody());
                } else if (request.getPathname().equals("/tradings")) {
                    return this.tradingController.createTradingDeal(request.getBody());
                } else if (request.getPathname().equals("/packages")) {
                    return this.cardController.createPackage(request.getBody());
                }
            }
            case PUT -> {
                String[] split = request.getPathname().split("/");
                //updateCard cardID
                if (request.getPathname().contains("/cards/")) {
                    return this.cardController.updateCard(parseId(split), request.getBody());
                }//updateUser userID
                else if (request.getPathname().contains("/users/")) {
                    return this.userController.updateUser(parseId(split), request.getBody());
                }//create + updateCard userID
                else if(request.getPathname().contains("/packages/buy/")){
                    //create package
                    //get cards where UID 0
                    //change UID for cards
                }
            }
            case DELETE -> {
                String[] split = request.getPathname().split("/");
                //deleteCard cardID
                if (request.getPathname().contains("/cards/")) {
                    return this.cardController.deleteCard(parseId(split));
                }//deleteUser userID
                else if (request.getPathname().contains("/users/")) {
                    return this.userController.deleteUser(parseId(split));
                }//deleteDemand demandID
                else if (request.getPathname().contains("/tradings/")) {
                    return this.tradingController.deleteTradingDeal(parseId(split));
                }
            }
            default -> {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Method Not Found\", \"data\": null }");

            }
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Method Not Found\", \"data\": null }");
    }

    public int parseId(String[] split){
        long lengthLong = Arrays.stream(split).count();
        int lengthInt = (int) lengthLong;
        int id = Integer.parseInt(split[lengthInt-1]);
        return id;
    }

}
