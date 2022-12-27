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

public class DeckDao {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public DeckDao(Connection connection) {
        setConnection(connection);
    }

    public ArrayList<Card> create(ArrayList<Card> cards) throws SQLException {
        //ArrayList<Card> result = new ArrayList<Card>();
        for(Card card: cards){
            String query = "INSERT INTO decks(CardID, UserID, CardName, Damage, Paused) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setString(0, card.getCardID());
            statement.setInt(1, card.getUserID());
            statement.setString(2, card.getName());
            statement.setInt(3, card.getDamage());
            statement.setBoolean(4, card.isPaused());

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

    public void delete(int UserID) throws SQLException{
        String query = "DELETE FROM cards WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1,UserID);
        statement.execute();
        statement.close();
    }


}
