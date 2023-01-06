package app.daos;

import app.models.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PackageDao{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public PackageDao(Connection connection){setConnection(connection);}

    public ArrayList<Card> create(ArrayList<Card> cards) throws SQLException {
        for(Card card: cards){
            String query = "INSERT INTO packages(CardID, UserID, CardName, Damage, Paused) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(1, card.getCardID());
            statement.setInt(2, card.getUserID());
            statement.setString(3, card.getName());
            statement.setInt(4, card.getDamage());
            statement.setBoolean(5, card.isPaused());

            statement.execute();
            statement.close();
        }
        return cards;
    }

    public ArrayList<Card> readPackage() throws SQLException {
        String query = "SELECT * FROM packages WHERE CardID IN (SELECT CardID FROM packages LIMIT 5)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        ResultSet res = statement.executeQuery();

        ArrayList<Card> pack = new ArrayList<Card>();
        ArrayList<String> ids = new ArrayList<String>();

        while(res.next()){
            Card tempCard = new Card(
                    res.getString(1), //CardID
                    res.getInt(2),  //UserID
                    res.getString(3), //Name
                    res.getInt(4), //Damage
                    res.getBoolean(5) //paused
            );
            pack.add(tempCard);
            ids.add(res.getString(1));
        }
        statement.close();
        delete(ids);
        return pack;
    }

    public void delete(ArrayList<String> ids) throws SQLException {
        String query = "DELETE FROM packages WHERE CardID IN (?, ?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, ids.get(0));
        statement.setString(2, ids.get(1));
        statement.setString(3, ids.get(2));
        statement.setString(4, ids.get(3));
        statement.setString(5, ids.get(4));
        statement.execute();
        statement.close();
    }
}
