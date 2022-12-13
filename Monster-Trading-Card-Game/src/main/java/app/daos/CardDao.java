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

public class CardDao implements Dao<Card>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public CardDao(Connection connection){setConnection(connection);}

    @Override
    public Card create(Card card) throws SQLException {
        String query = "INSERT INTO cards(UserID, Name, Damage, Paused) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, card.getUserID());
        statement.setString(2, card.getName());
        statement.setInt(3, card.getDamage());
        statement.setBoolean(4, card.isPaused());

        ResultSet res = statement.executeQuery();
        Card createdCard = new Card (
                res.getString(3), //Name
                res.getInt(4), //Damage
                res.getInt(1), //CardID
                res.getInt(2)  //UserID
        );
        statement.close();
        return createdCard;
    }

    @Override
    public ArrayList<Card> read() throws SQLException {return null;}

    public ArrayList<Card> readAllCardsFromUser(int uid) throws SQLException{
        ArrayList<Card> userCards = new ArrayList<Card>();
        String query = "SELECT * FROM cards WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, uid);

        ResultSet res = statement.executeQuery();
        while(res.next()){
            Card tempCard = new Card(
                    res.getString(3), //Name
                    res.getInt(4), //Damage
                    res.getInt(1), //CardID
                    res.getInt(2),  //UserID
                    res.getBoolean(5) //paused
            );
            userCards.add(tempCard);
        }
        statement.close();
        return userCards;
    }

    @Override
    public void update(Card card) throws SQLException {}

    public void updateCardUserID(int cid, int uid) throws SQLException{
        String query = "UPDATE cards SET UserID = ? WHERE CardID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, uid);
        statement.setInt(2, cid);

        ResultSet res = statement.executeQuery();
        statement.close();
    }

    public void updateCardPaused(int cid, Boolean paused) throws SQLException{
        String query = "UPDATE cards SET Paused = ? WHERE CardID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setBoolean(1, paused);
        statement.setInt(2, cid);

        ResultSet res = statement.executeQuery();
        statement.close();
    }

    @Override
    public void delete(Card card) throws SQLException {
        String query = "DELETE FROM cards WHERE CardID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, card.getCardID());

        ResultSet res = statement.executeQuery();
        statement.close();
    }
}
