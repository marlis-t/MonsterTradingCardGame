package app;

import app.controllers.CardController;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Request;
import app.server.Response;
import app.server.ServerApp;
import app.services.CardService;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.Arrays;

public class App implements ServerApp {
    @Setter(AccessLevel.PRIVATE)
    private CardController cardController;

    public App() {
        setCardController(new CardController(new CardService()));
    }

    public Response handleRequest(Request request) {

        switch (request.getMethod()) {
            case GET: {
                String[] split = request.getPathname().split("/");
                //getCardsByUID
                if (request.getPathname().contains("/cards/")) {
                    long length = Arrays.stream(split).count();
                    int leng = (int) length;
                    int id = Integer.parseInt(split[leng-1]);
                    return this.cardController.getCardsFromUserID(id);
                }//getCardByID
                else if(request.getPathname().contains("/card/")){
                    long length = Arrays.stream(split).count();
                    int leng = (int) length;
                    int id = Integer.parseInt(split[leng-1]);
                    return this.cardController.getCardById(id);
                }
            }
            case POST: {
                //createCard
                if (request.getPathname().equals("/card")) {
                    return this.cardController.createCard(request.getBody());
                }
            }
            case DELETE:{
                String[] split = request.getPathname().split("/");
                //deleteCard
                if(request.getPathname().contains("/card/")){
                    long length = Arrays.stream(split).count();
                    int leng = (int) length;
                    int id = Integer.parseInt(split[leng-1]);
                    return this.cardController.deleteCard(id);
                }
            }
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Method Not Found\", \"data\": null }");
    }
}
