package app.daos;

import app.models.BattleModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BattleDao {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public BattleDao(Connection connection){ setConnection(connection); }

    public void create(String requester) throws SQLException {
        String query = "INSERT INTO battles (Requester) VALUES (?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, requester);
        statement.execute();
        statement.close();
    }

    public ArrayList<BattleModel> readAll() throws SQLException {
        ArrayList<BattleModel> battles = new ArrayList<BattleModel>();
        String query = "SELECT * FROM battles";
        PreparedStatement statement = getConnection().prepareStatement(query);
        ResultSet res = statement.executeQuery();
        while(res.next()){
            BattleModel tempBattle = new BattleModel(
                    res.getInt(1), //BattleID
                    res.getString(2),  //Requester
                    res.getString(3), //Acceptor
                    res.getBoolean(4)
            );
            battles.add(tempBattle);
        }
        statement.close();
        return battles;
    }

    public BattleModel read(String requester) throws SQLException {
        String query = "SELECT * FROM battles WHERE Requester = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, requester);
        ResultSet res = statement.executeQuery();

        if(!res.next()){
            statement.close();
            return null;
        }
        BattleModel battle = new BattleModel(
                res.getInt(1),
                res.getString(2),
                res.getString(3),
                res.getBoolean(4)
        );
        statement.close();
        return battle;
    }

    public void update(BattleModel battle) throws SQLException {
        String query = "UPDATE battles SET Acceptor = ?, Ended = ? WHERE BattleID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, battle.getAcceptor());
        statement.setBoolean(2, battle.isEnded());
        statement.setInt(3, battle.getBattleID());
        statement.execute();
        statement.close();
    }

    public void delete(BattleModel battle) throws SQLException {
        String query = "DELETE FROM battles WHERE BattleID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, battle.getBattleID());
        statement.execute();
        statement.close();
    }
}
