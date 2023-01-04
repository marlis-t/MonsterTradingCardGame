package app.daos;

import app.models.UserModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDao implements Dao<UserModel>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public UserDao(Connection connection){setConnection(connection);}

    @Override
    public UserModel create(UserModel user) throws SQLException {
        String query = "INSERT INTO users(Username, Password, Coins, Score, GamesPlayed, Bio, Image, AuthToken) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setInt(3, user.getCoins());
        statement.setInt(4, user.getScore());
        statement.setInt(5, user.getGamesPlayed());
        statement.setString(6, user.getBio());
        statement.setString(7, user.getImage());
        statement.setString(8, user.getAuthToken());

        statement.execute();
        statement.close();
        user.setPassword("");
        return user;
    }


    @Override
    public ArrayList<UserModel> readAll() throws SQLException {
        ArrayList<UserModel> userList = new ArrayList<UserModel>();
        String query = "SELECT * FROM users";
        PreparedStatement statement = getConnection().prepareStatement(query);

        ResultSet res = statement.executeQuery();
        while(res.next()){
            UserModel tempUser = new UserModel(
                    res.getInt(1), //Uid
                    res.getString(2), //Username
                    "", //Password
                    res.getInt(4), //Coins
                    res.getInt(5), //Score
                    res.getInt(6), //GamesPlayed
                    res.getString(7), //Bio
                    res.getString(8), //Image
                    res.getString(9) //authToken
            );
            userList.add(tempUser);
        }
        statement.close();
        return userList;
    }

    @Override
    public UserModel read(String username) throws SQLException{
        String query = "SELECT * FROM users WHERE Username = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, username);

        ResultSet res = statement.executeQuery();
        if (!res.next()){
            statement.close();
            return null;
        }
        UserModel foundUser = new UserModel (
                res.getInt(1), //Uid
                res.getString(2), //Username
                "", //Password
                res.getInt(4), //Coins
                res.getInt(5), //Score
                res.getInt(6), //GamesPlayed
                res.getString(7), //Bio
                res.getString(8), //Image
                res.getString(9) //authToken
        );
        statement.close();
        return foundUser;
    }

    public Boolean checkCredentials(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE Username = ? AND Password = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);

        ResultSet res = statement.executeQuery();
        if (!res.next()){
            statement.close();
            return false;
        }
        statement.close();
        return true;
    }

    public ArrayList<String> readAuthToken() throws SQLException{
        ArrayList<String> authTokenList = new ArrayList<String>();
        String query = "SELECT AuthToken FROM users";
        PreparedStatement statement = getConnection().prepareStatement(query);

        ResultSet res = statement.executeQuery();
        String temp;
        while(res.next()){
            temp = res.getString(1);
            authTokenList.add(temp);
        }
        statement.close();
        return authTokenList;
    }
    @Override
    public void update(UserModel user) throws SQLException{
        String query = "UPDATE users SET Coins = ?, Score = ?, GamesPlayed = ?, Bio = ?, Image = ?, AuthToken = ? WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, user.getCoins());
        statement.setInt(2, user.getScore());
        statement.setInt(3, user.getGamesPlayed());
        statement.setString(4, user.getBio());
        statement.setString(5, user.getImage());
        statement.setString(6, user.getAuthToken());

        statement.setInt(7, user.getUserID());

        statement.execute();
        statement.close();
    }

    @Override
    public void delete(UserModel user) throws SQLException {
        String query = "DELETE FROM users WHERE UserID = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setInt(1, user.getUserID());

        statement.execute();
        statement.close();
    }
}
