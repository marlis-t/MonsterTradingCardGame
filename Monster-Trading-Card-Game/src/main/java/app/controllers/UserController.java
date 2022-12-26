package app.controllers;

import app.daos.UserDao;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import app.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import user.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class UserController extends Controller{

    private UserService userService;
    private UserDao userDao;

    public UserController(UserDao userDao) {
        setUserDao(userDao);
    }
    //GET /users/username
    public Response getUserByName(String username) {
        User user;
        try {
            user = getUserDao().read(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(user == null){
            return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
        }
        try {
            String userDataJSON = getObjectMapper().writeValueAsString(user);
            return sendResponse(userDataJSON, "null", HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //GET /stats
    public Response getStats(String auth){
        String username = auth.split("-")[0];
        User user;
        try {
            user = getUserDao().read(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(user == null){
            return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
        }
        String stats = "{ \"Score\": \"" + user.getScore() + "\", \"Games played\": \"" + user.getGamesPlayed() + "\" }";
        return sendResponse(stats, "null", HttpStatus.OK);
    }

    //GET /scores
    public Response getScoreboard(String auth){
        ArrayList<String> auths;
        ArrayList<User> users;
        try {
            users = getUserDao().readAll();
            auths = getUserDao().readAuthToken();
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if(users.isEmpty()){
            return sendResponse("null", "No Users found", HttpStatus.NOT_FOUND);
        }
        if(!auths.contains(auth)){
            return sendResponse("null", "Incorrect Token", HttpStatus.UNAUTHORIZED);
        }
        users.sort(Comparator.comparing(User::getScore).reversed());
        StringBuilder scoreData = new StringBuilder();
        scoreData.append("[");
        for(User user : users){
            scoreData.append(user.showScore()).append(", ");
        }
        scoreData.append("]");
        return sendResponse(scoreData.toString(), "null", HttpStatus.OK);
    }

    //GET /users
    public Response getAllUsers() {
        ArrayList<User> users;
        try {
            users = getUserDao().readAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if(users.isEmpty()){
            return sendResponse("null", "No Users found", HttpStatus.NOT_FOUND);

        }
        /*StringBuilder userData = new StringBuilder();
        for(User user : users){
            userData.append(user.showUserData());
        }*/
        try {
            String userDataJSON = getObjectMapper().writeValueAsString(users);
            return sendResponse(userDataJSON, "null", HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
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
            return sendResponse("null", "Username or password not set", HttpStatus.BAD_REQUEST);
        }
        try{
            //push user to db
            User user = new User(username, password);
            //check if username already in use
            ArrayList<User> allUsers;
            allUsers = getUserDao().readAll();
            for(User temp : allUsers){
                if(Objects.equals(user.getUsername(), temp.getUsername())){
                    return sendResponse("null", "Username already in use", HttpStatus.BAD_REQUEST);
                }
            }
            User createdUser = getUserDao().create(user);
            String userDataJSON = getObjectMapper().writeValueAsString(createdUser);
            return sendResponse(userDataJSON, "null", HttpStatus.CREATED);

        }catch(JsonProcessingException | SQLException e){
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
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
            return sendResponse("null", "Username or password not set", HttpStatus.BAD_REQUEST);
        }
        try {
            User user = getUserDao().read(username);
            if (!getUserDao().checkCredentials(username, password)) {
                return sendResponse("null", "Incorrect User-credentials", HttpStatus.NOT_FOUND);
            }
            String token = user.getUsername() + "-mtcgToken";
            user.setAuthToken(token);
            //push token to db
            getUserDao().update(user);
            return sendResponse("User logged in", "null", HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //DELETE /users/username
    public Response deleteUser(String username) {
        User userToDelete = null;
        try {
            userToDelete = getUserDao().read(username);
            if (userToDelete == null) {
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
            }
            getUserDao().delete(userToDelete);
        }catch (SQLException e) {
            e.printStackTrace();
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return sendResponse("User deleted", "null", HttpStatus.OK);
    }
    //PUT /users/username *****
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
        User oldUser;
        User newUser;
        //beg. bei 1 und dann +4 sind die deskriptoren
        try {
            oldUser = getUserDao().read(username);
            if (oldUser == null) {
                return sendResponse("null", "User does not exist", HttpStatus.NOT_FOUND);
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
            return sendResponse("null", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return sendResponse("User was updated", "null", HttpStatus.OK);
    }
    public Response sendResponse(String data, String error, HttpStatus status){
        return new Response(
                status,
                ContentType.JSON,
                "{ \"data\": \"" + data + "\", \"error\": " + error + " }"
        );
    }
}
