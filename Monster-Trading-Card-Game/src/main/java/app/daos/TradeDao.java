package app.daos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import tradingDeal.TradingDeal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TradeDao implements Dao<TradingDeal>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public TradeDao(Connection connection){setConnection(connection);}

    @Override
    public TradingDeal create(TradingDeal trade) throws SQLException {
        String query = "INSERT INTO tradingDeals (TradeID, UserID, CardToTradeID, MinDamage, Type) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1,trade.getTradeID());
        statement.setInt(2, trade.getUserID());
        statement.setString(3, trade.getCardToTradeID());
        statement.setInt(4, trade.getMinDamage());
        statement.setString(5, trade.getType().toString());

        statement.execute();
        statement.close();
        return trade;

    }
    @Override
    public ArrayList<TradingDeal> readAll() throws SQLException {
        ArrayList<TradingDeal> tradeList = new ArrayList<TradingDeal>();
        String query = "SELECT * FROM tradingDeals";
        PreparedStatement statement = getConnection().prepareStatement(query);

        ResultSet res = statement.executeQuery();
        while(res.next()){
            TradingDeal tempTrade = new TradingDeal(
                    res.getString(1), //ID
                    res.getInt(2), //UserID
                    res.getString(3), //CardToTradeID
                    res.getInt(4), //MinDamage
                    res.getString(5) //Type

            );
            tradeList.add(tempTrade);
        }
        statement.close();
        return tradeList;
    }
    @Override
    public TradingDeal read(String tradeID) throws SQLException{
        String query = "SELECT * FROM tradingDeals WHERE TradeID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, tradeID);

        ResultSet res = statement.executeQuery();
        if (!res.next()){
            statement.close();
            return null;
        }
        TradingDeal foundTrade = new TradingDeal (
                res.getString(1), //ID
                res.getInt(2), //UserID
                res.getString(3), //CardToTradeID
                res.getInt(4), //MinDamage
                res.getString(5) //Type
        );
        statement.close();
        return foundTrade;
    }
    @Override
    public void update(TradingDeal trade){}
    @Override
    public void delete(TradingDeal trade) throws SQLException{
        String query = "DELETE FROM tradingDeals WHERE TradeID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, trade.getTradeID());

        statement.execute();
        statement.close();
    }
}
