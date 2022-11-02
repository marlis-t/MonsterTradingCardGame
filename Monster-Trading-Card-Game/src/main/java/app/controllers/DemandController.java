package app.controllers;

import app.http.ContentType;
import app.http.HttpStatus;
import app.models.DemandModel;
import app.server.Response;
import app.services.DemandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class DemandController extends Controller{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private DemandService demandService;

    public DemandController(DemandService demandService){
        setDemandService(demandService);
    }

    public Response getDemandToOffer(int Uid){
        try {
            DemandModel demandData = getDemandService().getDemandToOffer(Uid);
            if(demandData == null){
                throw new IllegalStateException("No demand for an offer");
            }
            String demandDataJSON = getObjectMapper().writeValueAsString(demandData);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + demandDataJSON + ", \"error\": null }"
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
    public Response createDemand(String body){
        try{
            DemandModel demand = getObjectMapper().readValue(body, DemandModel.class);
            if(!getDemandService().createDemand(demand)){
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"Demand for this Uid already exists\", \"data\": null }"
                );
            }
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + demand + ", \"error\": null }"
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
    public Response deleteDemand(int Uid){
        DemandModel demand = getDemandService().getDemandToOffer(Uid);
        if(demand == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No Demand for this User\", \"data\": null }"
            );
        }
        getDemandService().deleteDemand(Uid);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": Demand of User #" + Uid + " deleted" + ", \"error\": null }"
        );
    }
}
