package app;

import app.controllers.CardController;
import app.controllers.TradingController;
import app.controllers.UserController;
import app.daos.CardDao;
import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Request;
import app.server.Response;
import app.server.ServerApp;
import app.services.CardService;
import app.services.DatabaseService;
import app.services.TradingService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private CardController cardController;
    private UserController userController;
    private TradingController tradingController;
    private Connection connection;

    public App() {
        try {
            setConnection(new DatabaseService().getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setCardController(new CardController(new CardService(), new CardDao(getConnection())));
        setUserController(new UserController(new UserDao(getConnection())));
        setTradingController(new TradingController(new TradingService()));
    }

    public Response handleRequest(Request request) {
        System.out.println(request.getPathname());
        System.out.println(request.getMethod());
        System.out.println(request.getParameters());
        switch (request.getMethod()) {
            case GET -> {
                String[] split = request.getPathname().split("/");
                //getCardsByUID
                if (request.getPathname().contains("/cards/users/")) {
                    return this.cardController.getCardsFromUserID(parseId(split));
                }//getAllOfferedCards
                else if (request.getPathname().contains("/tradings/")) {
                    return this.tradingController.getAllTradingDeals(parseId(split));
                }//getAllUsers ******
                else if (request.getPathname().equals("/users")) {//
                    return getUserController().getAllUsers();
                }//getCardByID
                else if (request.getPathname().contains("/cards/IDs/")) {
                    return this.cardController.getCardById(parseId(split));
                }//getUserByName *****
                else if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    return getUserController().getUserByName(parseUsername(split));
                }
            }
            case POST -> {
                //createCard
                if (request.getPathname().equals("/cards")) {
                    return this.cardController.createCard(request.getBody());
                }//createUser *****
                else if (request.getPathname().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                } //login user *****
                else if (request.getPathname().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody());
                }
                else if (request.getPathname().equals("/tradings")) {
                    return this.tradingController.createTradingDeal(request.getBody());
                }
                else if (request.getPathname().equals("/packages")) {
                    return this.cardController.createPackage(request.getBody());
                }
            }
            case PUT -> {
                String[] split = request.getPathname().split("/");
                //updateCard cardID
                if (request.getPathname().contains("/cards/")) {
                    return this.cardController.updateCard(parseId(split), request.getBody());
                }//updateUser by name *****
                else if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    return getUserController().updateUser(parseUsername(split), request.getBody());
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
                }//deleteUser by name *****
                else if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    return getUserController().deleteUser(parseUsername(split));
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
    public String parseUsername(String[] split){
        int length = (int) Arrays.stream(split).count();
        return split[length-1];
    }

}
