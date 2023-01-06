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

public class CardDao implements Dao<Card>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public CardDao(Connection connection){setConnection(connection);}

    @Override
    public Card create(Card card) throws SQLException {
        String query = "INSERT INTO cards(CardID, UserID, CardName, Damage, Paused) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, card.getCardID());
        statement.setInt(2, card.getUserID());
        statement.setString(3, card.getName());
        statement.setInt(4, card.getDamage());
        statement.setBoolean(5, card.isPaused());

        statement.execute();
        /*
        Card createdCard = new Card (
                res.getString(1), //CardID
                res.getInt(2),  //UserID
                res.getString(3), //Name
                res.getInt(4), //Damage
                res.getBoolean(5) //paused
        );
         */
        statement.close();
        return card;
    }

    @Override
    public Card read(String id) throws SQLException {
        String query = "SELECT * FROM cards WHERE cardID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, id);

        ResultSet res = statement.executeQuery();
        if(!res.next()){
            statement.close();
            return null;
        }
        Card foundCard = new Card (
                res.getString(1), //CardID
                res.getInt(2),  //UserID
                res.getString(3), //Name
                res.getInt(4), //Damage
                res.getBoolean(5) //paused
        );
        statement.close();
        return foundCard;
    }

    @Override
    public ArrayList<Card> readAll() throws SQLException {
        return null;
    }

    public ArrayList<Card> readAllCardsFromUser(int uid) throws SQLException{
        ArrayList<Card> userCards = new ArrayList<Card>();
        String query = "SELECT * FROM cards WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, uid);

        ResultSet res = statement.executeQuery();
        while(res.next()){
            Card tempCard = new Card(
                    res.getString(1), //CardID
                    res.getInt(2),  //UserID
                    res.getString(3), //Name
                    res.getInt(4), //Damage
                    res.getBoolean(5) //paused
            );
            userCards.add(tempCard);
        }
        statement.close();
        return userCards;
    }

    @Override
    public void update(Card card) throws SQLException {
        String query = "UPDATE cards SET UserID = ?, Paused = ? WHERE CardID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, card.getUserID());
        statement.setBoolean(2, card.isPaused());
        statement.setString(3, card.getCardID());

        statement.execute();
        statement.close();
    }

    @Override
    public void delete(Card card) throws SQLException {
        String query = "DELETE FROM cards WHERE CardID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, card.getCardID());

        statement.execute();
        statement.close();
    }
}
