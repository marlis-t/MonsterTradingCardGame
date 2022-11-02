package app.services;

import app.models.DemandModel;
import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;

public class DemandService {
    @Setter(AccessLevel.PRIVATE)
    public ArrayList<DemandModel> demandData;

    public DemandService(){
        demandData.add(new DemandModel(1, 15, ELEMENT.NORMAL, TYPE.SPELL ));
        demandData.add(new DemandModel(3, 0, ELEMENT.WATER, TYPE.SPELL));
    }
    public DemandModel getDemandToOffer(int Uid){
        for(DemandModel demand : demandData){
            if(demand.getUserID() == Uid){
                return demand;
            }
        }
        return null;
    }
    public boolean createDemand(DemandModel demand) {
        for(DemandModel aDemand : demandData){
            if(aDemand.getUserID() == demand.getUserID()){
                return false;
            }
        }
        demandData.add(demand);
        return true;
    }
    public void deleteDemand(int Uid){
        demandData.removeIf(demandModel -> Uid == demandModel.getUserID());
    }
}
