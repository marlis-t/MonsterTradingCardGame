package app;

import app.controllers.BattleController;
import app.controllers.CardController;
import app.controllers.TradingController;
import app.controllers.UserController;
import app.daos.*;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Request;
import app.server.Response;
import app.server.ServerApp;
import app.services.DatabaseService;
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
    private BattleController battleController;
    private Connection connection;

    public App() {
        try {
            setConnection(new DatabaseService().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setCardController(new CardController(new CardDao(getConnection()), new UserDao(getConnection()), new PackageDao(getConnection()), new DeckDao(getConnection())));
        setUserController(new UserController(new UserDao(getConnection())));
        setTradingController(new TradingController(new TradeDao(getConnection()), new UserDao(getConnection()), new CardDao(getConnection()), new DeckDao(getConnection())));
        setBattleController(new BattleController(new UserDao(getConnection()), new CardDao(getConnection()), new DeckDao(getConnection()), new BattleDao(getConnection())));
    }

    public Response handleRequest(Request request) {
        System.out.println(request.getPathname() + " Pathname");
        System.out.println(request.getMethod() + " Method");
        System.out.println(request.getAuthToken() + " Token");
        switch (request.getMethod()) {
            case GET -> {
                if(request.getAuthToken() == null){
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                }
                String[] split = request.getPathname().split("/");
                //getCardsFromUser *****
                if (request.getPathname().equals("/cards")) {
                    System.out.println("Req.Handler get cards from user");
                    return getCardController().getCardsFromUser(getUsernameFromToken(request.getAuthToken()));
                }//getDeckFromUser *****
                else if (request.getPathname().equals("/decks")) {
                    System.out.println("Req.Handler get deck from user");
                    boolean plain = false;
                    if(request.getParameters().contains("format=plain")){
                        System.out.println("plain format");
                        plain = true;
                    }
                    return getCardController().getDeckFromUser(getUsernameFromToken(request.getAuthToken()), plain);
                }//getAllUsers ******
                else if (request.getPathname().equals("/users")) {
                    System.out.println("Req.Handler get users");
                    return getUserController().getAllUsers();
                }//getUserByName *****
                else if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    System.out.println("Req.Handler get one user");
                    if(isAuthTokenCorrect(request.getAuthToken(), parseUsername(split))){
                        return getUserController().getUserByName(parseUsername(split));
                    }
                    return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{ \"error\": \"Incorrect Token\", \"data\": null }");
                }//getUserStats *****
                else if (request.getPathname().equals("/stats")){
                    System.out.println("Req.Handler get stats from user");
                    return getUserController().getStats(request.getAuthToken());
                }//getScores *****
                else if(request.getPathname().equals("/scores")){
                    System.out.println("Req.Handler get scores");
                    return getUserController().getScoreboard(request.getAuthToken());
                }//getTradingDeals *****
                else if(request.getPathname().equals("/tradings")){
                    System.out.println("Req.Handler get trades");
                    return getTradingController().getAllTradingDeals(request.getAuthToken());
                }
            }
            case POST -> {
                //createPackage *****
                if (request.getPathname().equals("/packages")) {
                    System.out.println("Req.Handler make pck");
                    if(isAuthTokenCorrect(request.getAuthToken(), "admin")){
                        return getCardController().createPackage(request.getBody());
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Admin Token\", \"data\": null }");
                }//buyPackage *****
                else if(request.getPathname().equals("/transactions/packages")){
                    System.out.println("Req.Handler buy pack");
                    if(request.getAuthToken() == null){
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                    }
                    return getCardController().acquirePackage(getUsernameFromToken(request.getAuthToken()));
                }//createUser *****
                else if (request.getPathname().equals("/users")) {
                    System.out.println("Req.Handler register user");
                    return getUserController().createUser(request.getBody());
                }//login user *****
                else if (request.getPathname().equals("/sessions")) {
                    System.out.println("Req.Handler login user");
                    return getUserController().loginUser(request.getBody());
                }//create TradingDeal
                else if (request.getPathname().equals("/tradings")) {
                    System.out.println("Req.Handler create trade");
                    if(request.getAuthToken() == null){
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                    }
                    return getTradingController().createTradingDeal(request.getBody(),getUsernameFromToken(request.getAuthToken()));
                }//carry out Trade
                else if (request.getPathname().matches("/tradings/([A-Za-z0-9]+(-[A-Za-z0-9]+)+)")){
                    String[] split = request.getPathname().split("/");
                    System.out.println("Req.Handler do trade");
                    if(request.getAuthToken() == null){
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                    }
                    return getTradingController().carryOutTradingDeal(parseUsername(split), request.getBody(), getUsernameFromToken(request.getAuthToken()));
                }//battle request
                else if (request.getPathname().equals("/battles")){
                    System.out.println("Req.Handler battle");
                    return getBattleController().haveBattle(getUsernameFromToken(request.getAuthToken()));
                }
            }
            case PUT -> {
                String[] split = request.getPathname().split("/");
                //updateUser by name *****
                if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    System.out.println("Req.Handler update user");
                    if(isAuthTokenCorrect(request.getAuthToken(), parseUsername(split))){
                        return getUserController().updateUser(parseUsername(split), request.getBody());
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"Incorrect/Missing Token\", \"data\": null }");
                } //assemble deck *****
                else if(request.getPathname().equals("/decks")){
                    System.out.println("Req.Handler set deck");
                    if(request.getAuthToken() == null){
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                    }
                    return getCardController().assembleDeck(request.getBody(), getUsernameFromToken(request.getAuthToken()));
                }
            }
            case DELETE -> {
                String[] split = request.getPathname().split("/");
                if(request.getAuthToken() == null){
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                }
                //deleteUser by name *****
                if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    System.out.println("Req.Handler delete user");
                    if(isAuthTokenCorrect(request.getAuthToken(), parseUsername(split))){
                        return getUserController().deleteUser(parseUsername(split));
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"Incorrect/Missing Token\", \"data\": null }");
                }//delete TradingDeal
                else if (request.getPathname().matches("/tradings/([A-Za-z0-9]+(-[A-Za-z0-9]+)+)")) {
                    return getTradingController().deleteTradingDeal(parseUsername(split), getUsernameFromToken(request.getAuthToken()));
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
