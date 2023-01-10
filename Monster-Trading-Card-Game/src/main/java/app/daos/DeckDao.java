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

public class DeckDao{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public DeckDao(Connection connection) {
        setConnection(connection);
    }

    public ArrayList<Card> create(ArrayList<Card> cards) throws SQLException {
        for(Card card: cards){
            String query = "INSERT INTO decks(CardID, UserID, CardName, Damage, Paused) VALUES (?, ?, ?, ?, ?)";
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

    public ArrayList<Card> readDeck(int UserID) throws SQLException {
        String query = "SELECT * FROM decks WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1,UserID);
        ResultSet res = statement.executeQuery();

        ArrayList<Card> deck = new ArrayList<Card>();
        while(res.next()){
            Card tempCard = new Card(
                    res.getString(1), //CardID
                    res.getInt(2),  //UserID
                    res.getString(3), //Name
                    res.getInt(4), //Damage
                    res.getBoolean(5) //paused
            );
            deck.add(tempCard);
        }
        statement.close();
        return deck;
    }

    public void updateUID(Card card) throws SQLException {
        String query = "UPDATE decks SET UserID = ? WHERE CardID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, card.getUserID());
        statement.setString(2, card.getCardID());

        statement.execute();
        statement.close();
    }

    public void delete(int UserID) throws SQLException{
        String query = "DELETE FROM decks WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1,UserID);
        statement.execute();
        statement.close();
    }
}
