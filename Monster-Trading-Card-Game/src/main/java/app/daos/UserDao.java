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
        String query = "INSERT INTO users(Username, Password, Coins, Score, GamesPlayed, Bio, Image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setInt(3, user.getCoins());
        statement.setInt(4, user.getScore());
        statement.setInt(5, user.getGamesPlayed());
        statement.setString(6, user.getBio());
        statement.setString(7, user.getImage());

        ResultSet res = statement.executeQuery();
        User createdUser = new User (
                res.getInt(1), //Uid
                res.getString(2), //Username
                res.getString(3), //Password
                res.getInt(4), //Coins
                res.getInt(5), //Score
                res.getInt(6), //GamesPlayed
                res.getString(7), //Bio
                res.getString(8), //Image
                null, //stack
                null, //deck
                null //tradingDeal
        );
        statement.close();
        return createdUser;
    }


    @Override
    public ArrayList<User> readAll() throws SQLException {
        ArrayList<User> userList = new ArrayList<User>();
        String query = "SELECT * FROM users";
        PreparedStatement statement = getConnection().prepareStatement(query);

        ResultSet res = statement.executeQuery();
        while(res.next()){
            User tempUser = new User(
                    res.getInt(1), //Uid
                    res.getString(2), //Username
                    res.getString(3), //Password
                    res.getInt(4), //Coins
                    res.getInt(5), //Score
                    res.getInt(6), //GamesPlayed
                    res.getString(7), //Bio
                    res.getString(8), //Image
                    null, //stack
                    null, //deck
                    null //tradingDeal
            );
            userList.add(tempUser);
        }
        return userList;
    }

    @Override
    public User read(String username) throws SQLException{
        String query = "SELECT * FROM users WHERE Username = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, username);

        ResultSet res = statement.executeQuery();
        User foundUser = new User (
                res.getInt(1), //Uid
                res.getString(2), //Username
                res.getString(3), //Password
                res.getInt(4), //Coins
                res.getInt(5), //Score
                res.getInt(6), //GamesPlayed
                res.getString(7), //Bio
                res.getString(8), //Image
                null, //stack
                null, //deck
                null //tradingDeal
        );
        statement.close();
        return foundUser;
        //add cards + tradingDeal afterwards
    }
    @Override
    public void update(User user) throws SQLException{
        String query = "UPDATE users SET Coins = ?, Score = ?, GamesPlayed = ?, Bio = ?, Image = ? WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, user.getCoins());
        statement.setInt(2, user.getScore());
        statement.setInt(3, user.getGamesPlayed());
        statement.setString(4, user.getBio());
        statement.setString(5, user.getImage());

        statement.setInt(6, user.getUserID());

        statement.executeQuery();
        statement.close();
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM users WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, user.getUserID());

        statement.executeQuery();
        statement.close();
    }
}
