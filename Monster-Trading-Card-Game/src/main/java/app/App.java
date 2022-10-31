package app;

import app.controllers.CardController;
import app.controllers.UserController;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Request;
import app.server.Response;
import app.server.ServerApp;
import app.services.CardService;
import app.services.UserService;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.Arrays;

public class App implements ServerApp {
    @Setter(AccessLevel.PRIVATE)
    private CardController cardController;
    @Setter(AccessLevel.PRIVATE)
    private UserController userController;

    public App() {
        setCardController(new CardController(new CardService()));
        setUserController(new UserController(new UserService()));
    }

    public Response handleRequest(Request request) {
        switch (request.getMethod()) {
            case GET: {
                String[] split = request.getPathname().split("/");
                //getCardsByUID
                if (request.getPathname().contains("/cards/")) {
                    return this.cardController.getCardsFromUserID(parseId(split));
                }//getCardByID
                else if(request.getPathname().contains("/card/")){
                    return this.cardController.getCardById(parseId(split));
                }//getUserByID
                else if(request.getPathname().contains("/user/")){
                    return this.userController.getUserById(parseId(split));
                }//getAllUsersExceptSelf
                else if(request.getPathname().contains("/users/")){
                    return this.userController.getAllUsersExceptSelf(parseId(split));
                }//getUserByCredentials
                else if(request.getPathname().contains("/user/login/")){
                    String[] creds = parseUserNamePW(split);
                    return this.userController.getUserByCredentials(creds[0], creds[1]);
                }
            }
            case POST: {
                //createCard
                if (request.getPathname().equals("/card")) {
                    return this.cardController.createCard(request.getBody());
                }//createUser
                else if(request.getPathname().equals("/user")){
                    return this.userController.createUser(request.getBody());
                }
            }
            case PUT: {
                String[] split = request.getPathname().split("/");
                //updateCard
                if(request.getPathname().contains("/card/")){
                    return this.cardController.updateCard(parseId(split), request.getBody());
                }//updateUser
                else if(request.getPathname().contains("/user/")){
                    return this.userController.updateUser(parseId(split), request.getBody());
                }
            }
            case DELETE:{
                String[] split = request.getPathname().split("/");
                //deleteCard
                if(request.getPathname().contains("/card/")){
                    return this.cardController.deleteCard(parseId(split));
                }else if(request.getPathname().contains("/user/")){
                    return this.userController.deleteUser(parseId(split));
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
        creds[1] = split[leng-1];
        return creds;
    }
}
