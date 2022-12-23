package app.daos;

import card.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PackageDao {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public PackageDao(Connection connection){setConnection(connection);}

    public ArrayList<Card> create(ArrayList<Card> cards) throws SQLException {
        ArrayList<Card> result = new ArrayList<Card>();
        for(Card card: cards){
            String query = "INSERT INTO packages(CardID, UserID, Name, Damage, Paused) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(0, card.getCardID());
            statement.setInt(1, card.getUserID());
            statement.setString(2, card.getName());
            statement.setInt(3, card.getDamage());
            statement.setBoolean(4, card.isPaused());

            ResultSet res = statement.executeQuery();
            Card createdCard = new Card (
                    res.getString(1), //CardID
                    res.getInt(2),  //UserID
                    res.getString(3), //Name
                    res.getInt(4), //Damage
                    res.getBoolean(5) //paused
            );
            result.add(createdCard);
            statement.close();
        }
        return result;
    }

    public ArrayList<Card> readPackage() throws SQLException {
        String query = "SELECT * FROM packages WHERE CardID IN (SELECT CardID FROM packages LIMIT 5)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        ResultSet res = statement.executeQuery();

        ArrayList<Card> pack = new ArrayList<Card>();
        while(res.next()){
            Card tempCard = new Card(
                    res.getString(1), //CardID
                    res.getInt(2),  //UserID
                    res.getString(3), //Name
                    res.getInt(4), //Damage
                    res.getBoolean(5) //paused
            );
            pack.add(tempCard);
        }
        statement.close();
        return pack;
    }

    public void delete() throws SQLException {
        String query = "DELETE FROM packages WHERE CardID IN (SELECT CardID FROM packages LIMIT 5)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.execute();
        statement.close();
    }
}
