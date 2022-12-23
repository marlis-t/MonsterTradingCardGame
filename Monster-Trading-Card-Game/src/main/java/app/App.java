package app;

import app.controllers.CardController;
import app.controllers.TradingController;
import app.controllers.UserController;
import app.daos.CardDao;
import app.daos.PackageDao;
import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Request;
import app.server.Response;
import app.server.ServerApp;
import app.services.DatabaseService;
import app.services.TradingService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

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
        setCardController(new CardController(new CardDao(getConnection()), new UserDao(getConnection()), new PackageDao(getConnection())));
        setUserController(new UserController(new UserDao(getConnection())));
        setTradingController(new TradingController(new TradingService()));
    }

    public Response handleRequest(Request request) {
        System.out.println(request.getPathname());
        System.out.println(request.getMethod());
        System.out.println(request.getAuthToken());
        switch (request.getMethod()) {
            case GET -> {
                String[] split = request.getPathname().split("/");
                //getCardsFromUser *****
                if (request.getPathname().equals("/cards")) {
                    return getCardController().getCardsFromUser(getUsernameFromToken(request.getAuthToken()));
                }//getAllOfferedCards
                else if (request.getPathname().contains("/tradings/")) {
                    return this.tradingController.getAllTradingDeals(parseId(split));
                }//getAllUsers ******
                else if (request.getPathname().equals("/users")) {
                    return getUserController().getAllUsers();
                }//getUserByName *****
                else if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    if(isAuthTokenCorrect(request.getAuthToken(), parseUsername(split))){
                        return getUserController().getUserByName(parseUsername(split));
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"Incorrect Token\", \"data\": null }");
                }//getUserStats *****
                else if (request.getPathname().equals("/stats")){
                    return getUserController().getStats(request.getAuthToken());
                }//getScores *****
                else if(request.getPathname().equals("/scores")){
                    return getUserController().getScoreboard(request.getAuthToken());
                }
            }
            case POST -> {
                //createPackage *****
                if (request.getPathname().equals("/packages")) {
                    if(isAuthTokenCorrect(request.getAuthToken(), "admin")){
                        return getCardController().createPackage(request.getBody());
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Admin Token\", \"data\": null }");
                }//buyPackage *****
                else if(request.getPathname().equals("/packages/transactions")){
                    return getCardController().acquirePackage(getUsernameFromToken(request.getAuthToken()));
                }//createUser *****
                else if (request.getPathname().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                }//login user *****
                else if (request.getPathname().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody());
                }
                else if (request.getPathname().equals("/tradings")) {
                    return this.tradingController.createTradingDeal(request.getBody());
                }
            }
            case PUT -> {
                String[] split = request.getPathname().split("/");
                //updateUser by name *****
                if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    if(isAuthTokenCorrect(request.getAuthToken(), parseUsername(split))){
                        return getUserController().updateUser(parseUsername(split), request.getBody());
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"Incorrect Token\", \"data\": null }");
                }
            }
            case DELETE -> {
                String[] split = request.getPathname().split("/");
                //deleteUser by name *****
                if (request.getPathname().matches("/users/[a-zA-Z]+")) {
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
    public String getUsernameFromToken(String auth){
        String[] splitAuth = auth.split("-");
        return splitAuth[0];
    }
    public boolean isAuthTokenCorrect(String auth, String username){
        //auth == username-mtcgToken
        String[] splitAuth = auth.split("-");
        return Objects.equals(splitAuth[0], username);
    }

}
