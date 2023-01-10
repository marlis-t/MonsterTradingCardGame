package app.controllers;

import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import app.models.UserModel;

import java.sql.SQLException;
import java.util.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class UserController extends Controller{
    public UserController(UserDao userDao) {
        super(userDao);
    }
    //GET /users/username
    public Response getUserByName(String username) {
        UserModel user;
        try {
            user = getUserDao().read(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
        if(user == null){
            return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
        }
        try {
            String userDataJSON = getObjectMapper().writeValueAsString(user);
            return sendResponseWithType(userDataJSON, "null", HttpStatus.OK, ContentType.JSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    //GET /stats
    public Response getStats(String auth){
        ArrayList<String> auths;
        String username = auth.split("-")[0];
        UserModel user;
        try {
            auths = getUserDao().readAuthToken();
            user = getUserDao().read(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
        if(!auths.contains(auth)){
            return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
        }
        if(user == null){
            return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
        }
        String stats = "{\"Score\": \"" + user.getScore() + "\", \"Games played\": \"" + user.getGamesPlayed() + "\"}";
        return sendResponseWithType(stats, "null", HttpStatus.OK, ContentType.JSON);
    }

    //GET /scores
    public Response getScoreboard(String auth){
        ArrayList<String> auths;
        ArrayList<UserModel> users;
        try {
            users = getUserDao().readAll();
            auths = getUserDao().readAuthToken();
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);

        }
        if(users.isEmpty()){
            return sendResponseWithType("null", "No Users found", HttpStatus.NOT_FOUND, ContentType.TEXT);
        }
        if(!auths.contains(auth)){
            return sendResponseWithType("null", "Incorrect Token", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
        }
        users.sort(Comparator.comparing(UserModel::getScore).reversed());
        StringBuilder scoreData = new StringBuilder();
        for(UserModel user : users){
            scoreData.append(user.showScore()).append(", ");
        }
        return sendResponseWithType(scoreData.toString(), "null", HttpStatus.OK, ContentType.JSON);
    }

    //GET /users
    public Response getAllUsers() {
        ArrayList<UserModel> users;
        try {
            users = getUserDao().readAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);

        }
        if(users.isEmpty()){
            return sendResponseWithType("null", "No Users found", HttpStatus.NOT_FOUND, ContentType.TEXT);

        }
        try {
            String userDataJSON = getObjectMapper().writeValueAsString(users);
            return sendResponseWithType(userDataJSON, "null", HttpStatus.OK, ContentType.JSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    //POST /users
    public Response createUser(String body) {
        String[] split = body.split("\"");
        String username = "";
        String password = "";
        //find username + pw if set in body
        int length = (int) Arrays.stream(split).count();
        for(int i = 0; i < length; i++){
            if(Objects.equals(split[i], "Username")){
                username = split[i+2];
            }else if(Objects.equals(split[i], "Password")){
                password = split[i+2];
            }
        }
        if(username.equals("") || password.equals("")){
            return sendResponseWithType("null", "Username or password not set", HttpStatus.BAD_REQUEST, ContentType.TEXT);
        }
        try{
            //push user to db
            UserModel user = new UserModel(username, password);
            //check if username already in use
            ArrayList<UserModel> allUsers;
            allUsers = getUserDao().readAll();
            for(UserModel temp : allUsers){
                if(Objects.equals(user.getUsername(), temp.getUsername())){
                    return sendResponseWithType("null", "Username already in use", HttpStatus.CONFLICT, ContentType.TEXT);
                }
            }
            UserModel createdUser = getUserDao().create(user);
            String userDataJSON = getObjectMapper().writeValueAsString(createdUser);
            return sendResponseWithType(userDataJSON, "null", HttpStatus.CREATED, ContentType.JSON);

        }catch(JsonProcessingException | SQLException e){
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    //POST /sessions
    public Response loginUser(String body){
        String[] split = body.split("\"");
        String username = "";
        String password = "";
        int length = (int) Arrays.stream(split).count();
        for(int i = 0; i < length; i++){
            if(Objects.equals(split[i], "Username")){
                username = split[i+2];
            }else if(Objects.equals(split[i], "Password")){
                password = split[i+2];
            }
        }
        if(username.equals("") || password.equals("")){
            return sendResponseWithType("null", "Username or password not set", HttpStatus.BAD_REQUEST, ContentType.TEXT);
        }
        try {
            UserModel user = getUserDao().read(username);
            if (!getUserDao().checkCredentials(username, password)) {
                return sendResponseWithType("null", "Incorrect User-credentials", HttpStatus.UNAUTHORIZED, ContentType.TEXT);
            }
            String token = user.getUsername() + "-mtcgToken";
            user.setAuthToken(token);
            //push token to db
            getUserDao().update(user);
            return sendResponseWithType(token, "null", HttpStatus.OK, ContentType.TEXT);
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
    }

    //DELETE /users/username
    public Response deleteUser(String username) {
        UserModel userToDelete = null;
        try {
            userToDelete = getUserDao().read(username);
            if (userToDelete == null) {
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            getUserDao().delete(userToDelete);
        }catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
        return sendResponseWithType("User deleted", "null", HttpStatus.OK, ContentType.TEXT);
    }
    //PUT /users/username
    public Response updateUser(String username, String body) {
        String[] split = body.split("\"");
        String newUsername = "";
        String newBio = "";
        String newImage = "";

        int length = (int) Arrays.stream(split).count();
        for(int i = 0; i < length; i++){
            if(Objects.equals(split[i], "Username")){
                newUsername = split[i+2];
            }else if(Objects.equals(split[i], "Bio")){
                newBio = split[i+2];
            }else if(Objects.equals(split[i], "Image")){
                newImage = split[i+2];
            }
        }
        UserModel oldUser;
        UserModel newUser;
        try {
            oldUser = getUserDao().read(username);
            if (oldUser == null) {
                return sendResponseWithType("null", "User does not exist", HttpStatus.NOT_FOUND, ContentType.TEXT);
            }
            newUser = oldUser;
            if (!Objects.equals(newBio, "")) {
                newUser.setBio(newBio);
            }
            if (!Objects.equals(newImage, "")) {
                newUser.setImage(newImage);
            }
            getUserDao().update(newUser);
        }catch (SQLException e) {
            e.printStackTrace();
            return sendResponseWithType("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT);
        }
        return sendResponseWithType("User was updated", "null", HttpStatus.OK, ContentType.TEXT);
    }
}
