package app.controllers;

import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import app.services.TradingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import user.TradingDeal;

import java.util.ArrayList;

public class TradingController extends Controller{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private TradingService tradingService;

    public TradingController(TradingService tradingService){
        setTradingService(tradingService);
    }

    public Response getTradingDeal(int Uid){
        try {
            TradingDeal trade = getTradingService().getTradingDealByUid(Uid);
            if(trade == null){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": No trading deals for this user , \"data\": null }"
                );
            }
            String tradeDataJSON = getObjectMapper().writeValueAsString(trade);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + tradeDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    public Response getAllTradingDeals(int Uid){
        try {
            ArrayList<TradingDeal> trades = getTradingService().getAllTrades(Uid);
            if(trades == null){
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{}"
                );
            }
            String tradeDataJSON = getObjectMapper().writeValueAsString(trades);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + tradeDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }
    public Response createTradingDeal(String body){
        try{
            TradingDeal trade = getObjectMapper().readValue(body, TradingDeal.class);
            if(!getTradingService().createTradingDeal(trade)){
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"TradingDeal for this Uid already exists\", \"data\": null }"
                );
            }
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + trade + ", \"error\": null }"
            );

        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }
    public Response deleteTradingDeal(int Uid){
        TradingDeal trade = getTradingService().getTradingDealByUid(Uid);
        if(trade == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No Demand for this User\", \"data\": null }"
            );
        }
        getTradingService().getTradingDealByUid(Uid);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": TradingDeal of User #" + Uid + " deleted" + ", \"error\": null }"
        );
    }
}
