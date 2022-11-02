package app;

import app.controllers.CardController;
import app.controllers.DemandController;
import app.controllers.UserController;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Request;
import app.server.Response;
import app.server.ServerApp;
import app.services.CardService;
import app.services.DemandService;
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
    private DemandController demandController;

    public App() {
        setCardController(new CardController(new CardService()));
        setUserController(new UserController(new UserService()));
        setDemandController(new DemandController(new DemandService()));
    }

    public Response handleRequest(Request request) {
        switch (request.getMethod()) {
            case GET: {
                String[] split = request.getPathname().split("/");
                //getCardsByUID
                if (request.getPathname().contains("/cards/Uid/")) {
                    return this.cardController.getCardsFromUserID(parseId(split));
                }//getAllOfferedCards
                else if(request.getPathname().contains("/cards/offers/")){
                    return this.cardController.getAllOfferedCards(parseId(split));
                }//getAllUsersExceptSelf
                else if(request.getPathname().contains("/users/all/")){//
                    return this.userController.getAllUsersExceptSelf(parseId(split));
                }//getCardByID
                else if(request.getPathname().contains("/cards/ID/")){
                    return this.cardController.getCardById(parseId(split));
                }//getUserByID
                else if(request.getPathname().contains("/users/ID/")){
                    return this.userController.getUserById(parseId(split));
                }
                else if(request.getPathname().contains("/demands/all/")){
                    return this.demandController.getDemandToOffer(parseId(split));
                }
            }
            case POST: {
                //createCard
                if (request.getPathname().equals("/cards")) {
                    return this.cardController.createCard(request.getBody());
                }//createUser
                else if(request.getPathname().equals("/users")){
                    return this.userController.createUser(request.getBody());
                }
                else if(request.getPathname().equals("/users/login")){//
                    return this.userController.getUserByCredentials(request.getBody());
                }
                else if(request.getPathname().equals("/demands")){
                    return this.demandController.createDemand(request.getBody());
                }
            }
            case PUT: {
                String[] split = request.getPathname().split("/");
                //updateCard
                if(request.getPathname().contains("/cards/")){
                    return this.cardController.updateCard(parseId(split), request.getBody());
                }//updateUser
                else if(request.getPathname().contains("/users/")){
                    return this.userController.updateUser(parseId(split), request.getBody());
                }
            }
            case DELETE:{
                String[] split = request.getPathname().split("/");
                //deleteCard
                if(request.getPathname().contains("/cards/")){
                    return this.cardController.deleteCard(parseId(split));
                }else if(request.getPathname().contains("/users/")){
                    return this.userController.deleteUser(parseId(split));
                }else if(request.getPathname().contains("/demands/")){
                    return this.demandController.deleteDemand(parseId(split));
                }
            }
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Method Not Found\", \"data\": null }");
    }

    public int parseId(String[] split){
        long length = Arrays.stream(split).count();
        int leng = (int) length;
        int id = Integer.parseInt(split[leng-1]);
        return id;
    }
    public String[] parseUserNamePW(String[] split) {
        long length = Arrays.stream(split).count();
        int leng = (int) length;
        String[] creds = new String[2];
        creds[0] = split[leng-2];
        creds[1] = new String(split[leng-1]);
        return creds;
    }
}
