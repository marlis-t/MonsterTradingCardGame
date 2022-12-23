package app.services;

import card.Enum.ELEMENT;
import card.Enum.TYPE;
import lombok.AccessLevel;
import lombok.Setter;
import user.TradingDeal;

import java.util.ArrayList;

public class TradingService {
    @Setter(AccessLevel.PRIVATE)
    public ArrayList<TradingDeal> tradingData;

    public TradingService(){
        setTradingData(new ArrayList<TradingDeal>());
        tradingData.add(new TradingDeal(1, 1, "1", "Waterspell", 10, 10,  ELEMENT.FIRE, TYPE.SPELL));
    }
    public TradingDeal getTradingDealByUid(int Uid){
        for(TradingDeal atrade : tradingData){
            if(atrade.getUserID() == Uid){
                return atrade;
            }
        }
        return null;
    }
    public ArrayList<TradingDeal> getAllTrades(int Uid){
        ArrayList<TradingDeal> trades = new ArrayList<TradingDeal>();
        for(TradingDeal atrade : tradingData){
            if(atrade.getUserID() != Uid){
                trades.add(atrade);
            }
        }
        return trades;
    }
    public boolean createTradingDeal(TradingDeal trade) {
        for(TradingDeal atrade : tradingData){
            if(atrade.getUserID() == trade.getUserID()){
                return false;
            }
        }
        tradingData.add(trade);
        return true;
    }
    public void createTradingDeal(int Uid){
        tradingData.removeIf(tradingDeal -> Uid == tradingDeal.getUserID());
    }
}
