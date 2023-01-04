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
        switch (request.getMethod()) {
            case GET -> {
                if(request.getAuthToken() == null){
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                }
                String[] split = request.getPathname().split("/");
                //getCardsFromUser *****
                if (request.getPathname().equals("/cards")) {
                    return getCardController().getCardsFromUser(getUsernameFromToken(request.getAuthToken()));
                }//getDeckFromUser *****
                else if (request.getPathname().equals("/decks")) {
                    boolean plain = request.getParameters().contains("format=plain");
                    return getCardController().getDeckFromUser(getUsernameFromToken(request.getAuthToken()), plain);
                }//getAllUsers ******
                else if (request.getPathname().equals("/users")) {
                    return getUserController().getAllUsers();
                }//getUserByName *****
                else if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    if(doesAuthTokenMatchWithUser(request.getAuthToken(), parseUsernameFromPath(split))){
                        return getUserController().getUserByName(parseUsernameFromPath(split));
                    }
                    return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{ \"error\": \"Incorrect Token\", \"data\": null }");
                }//getUserStats *****
                else if (request.getPathname().equals("/stats")){
                    return getUserController().getStats(request.getAuthToken());
                }//getScores *****
                else if(request.getPathname().equals("/scores")){
                    return getUserController().getScoreboard(request.getAuthToken());
                }//getTradingDeals *****
                else if(request.getPathname().equals("/tradings")){
                    return getTradingController().getAllTradingDeals(request.getAuthToken());
                }
            }
            case POST -> {
                //createPackage *****
                if (request.getPathname().equals("/packages")) {
                    if(doesAuthTokenMatchWithUser(request.getAuthToken(), "admin")){
                        return getCardController().createPackage(request.getBody());
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Admin Token\", \"data\": null }");
                }//buyPackage *****
                else if(request.getPathname().equals("/transactions/packages")){
                    if(request.getAuthToken() == null){
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                    }
                    return getCardController().acquirePackage(getUsernameFromToken(request.getAuthToken()));
                }//createUser *****
                else if (request.getPathname().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                }//login user *****
                else if (request.getPathname().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody());
                }//create TradingDeal *****
                else if (request.getPathname().equals("/tradings")) {
                    if(request.getAuthToken() == null){
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                    }
                    return getTradingController().createTradingDeal(request.getBody(),getUsernameFromToken(request.getAuthToken()));
                }//carry out Trade *****
                else if (request.getPathname().matches("/tradings/([A-Za-z0-9]+(-[A-Za-z0-9]+)+)")){
                    String[] split = request.getPathname().split("/");
                    if(request.getAuthToken() == null){
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"No Token set\", \"data\": null }");
                    }
                    return getTradingController().carryOutTradingDeal(parseUsernameFromPath(split), request.getBody(), getUsernameFromToken(request.getAuthToken()));
                }//battle request *****
                else if (request.getPathname().equals("/battles")){
                    return getBattleController().haveBattle(getUsernameFromToken(request.getAuthToken()));
                }
            }
            case PUT -> {
                String[] split = request.getPathname().split("/");
                //updateUser by name *****
                if (request.getPathname().matches("/users/[a-zA-Z]+")) {
                    if(doesAuthTokenMatchWithUser(request.getAuthToken(), parseUsernameFromPath(split))){
                        return getUserController().updateUser(parseUsernameFromPath(split), request.getBody());
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"Incorrect/Missing Token\", \"data\": null }");
                } //assemble deck *****
                else if(request.getPathname().equals("/decks")){
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
                    if(doesAuthTokenMatchWithUser(request.getAuthToken(), parseUsernameFromPath(split))){
                        return getUserController().deleteUser(parseUsernameFromPath(split));
                    }
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{ \"error\": \"Incorrect/Missing Token\", \"data\": null }");
                }//delete TradingDeal *****
                else if (request.getPathname().matches("/tradings/([A-Za-z0-9]+(-[A-Za-z0-9]+)+)")) {
                    return getTradingController().deleteTradingDeal(parseUsernameFromPath(split), getUsernameFromToken(request.getAuthToken()));
                }
            }
            default -> {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Method Not Found\", \"data\": null }");

            }
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Method Not Found\", \"data\": null }");
    }
    public String parseUsernameFromPath(String[] split){
        //username always last
        int length = (int) Arrays.stream(split).count();
        return split[length-1];
    }
    public String getUsernameFromToken(String auth){
        String[] splitAuth = auth.split("-");
        return splitAuth[0];
    }
    public boolean doesAuthTokenMatchWithUser(String auth, String username){
        //auth == username-mtcgToken
        String[] splitAuth = auth.split("-");
        return Objects.equals(splitAuth[0], username);
    }

}
