package app.daos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDao implements Dao<User>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public UserDao(Connection connection){setConnection(connection);}


    @Override
    public User create(User user) throws SQLException {
        String query = "INSERT INTO users(Username, Coins, Score, GamesPlayed) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, user.getUsername());
        statement.setInt(2, user.getCoins());
        statement.setInt(3, user.getScore());
        statement.setInt(4, user.getGamesPlayed());

        ResultSet res = statement.executeQuery();
        User createdUser = new User (
                res.getInt(1), //Uid
                res.getString(2), //Username
                res.getInt(3), //Coins
                res.getInt(4), //Score
                res.getInt(5), //GamesPlayed
                null, //stack
                null //tradingDeal
        );
        statement.close();
        return createdUser;
    }

    @Override
    public ArrayList<User> read() throws SQLException {return null;}

    public User readUserByUid(int uid) throws SQLException{
        String query = "SELECT * FROM users WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, uid);

        ResultSet res = statement.executeQuery();
        User foundUser = new User (
                res.getInt(1), //Uid
                res.getString(2), //Username
                res.getInt(3), //Coins
                res.getInt(4), //Score
                res.getInt(5), //GamesPlayed
                null, //stack
                null //tradingDeal
        );
        statement.close();
        return foundUser;
        //add cards + tradingDeal afterwards
    }

    @Override
    public void update(User user) throws SQLException {}

    public void updateUserName(int uid, String name) throws SQLException{
        String query = "UPDATE users SET Username = ? WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, name);
        statement.setInt(2, uid);

        ResultSet res = statement.executeQuery();
        statement.close();
    }
    public void updateUserCoins(int uid, int coins) throws SQLException{
        String query = "UPDATE users SET Coins = ? WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, coins);
        statement.setInt(2, uid);

        ResultSet res = statement.executeQuery();
        statement.close();
    }
    public void updateUserScore(int uid, int score) throws SQLException{
        String query = "UPDATE users SET Score = ? WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, score);
        statement.setInt(2, uid);

        ResultSet res = statement.executeQuery();
        statement.close();
    }
    public void updateUserGamesPlayed(int uid, int gamesPlayed) throws SQLException{
        String query = "UPDATE users SET GamesPlayed = ? WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, gamesPlayed);
        statement.setInt(2, uid);

        ResultSet res = statement.executeQuery();
        statement.close();
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM users WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, user.getUserID());

        ResultSet res = statement.executeQuery();
        statement.close();
    }
}
