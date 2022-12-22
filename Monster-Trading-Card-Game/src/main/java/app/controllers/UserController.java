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
        User user = null;
        try {
            user = getUserDao().read(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
        if(user == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No User with this Name\", \"data\": null }"
            );
        }
        try {
            String userDataJSON = getObjectMapper().writeValueAsString(user);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + userDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }

    //GET /stats
    public Response getStats(String auth){
        String username = auth.split("-")[0];
        User user = null;
        try {
            user = getUserDao().read(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
        if(user == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No User with this Name\", \"data\": null }"
            );
        }
        String stats =
                "{ \"Score\": \"" + user.getScore() + "\", \"Games played\": \"" + user.getGamesPlayed() + "\" }";

        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + stats + ", \"error\": null }"
        );
    }

    //GET /scores
    public Response getScoreboard(String auth){
        ArrayList<String> auths = new ArrayList<>();
        ArrayList<User> users;
        try {
            auths = getUserDao().readAuthToken();
            users = getUserDao().readAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
        if(users.isEmpty()){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No Users found\", \"data\": null }"
            );
        }
        if(!auths.contains(auth)){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"error\": \"Incorrect Token\", \"data\": null }"
            );
        }
        users.sort(Comparator.comparing(User::getScore).reversed());
        StringBuilder scoreData = new StringBuilder();
        scoreData.append("[");
        for(User user : users){
            scoreData.append(user.showScore()).append(", ");
        }
        scoreData.append("]");
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + scoreData + ", \"error\": null }"
        );
    }

    //GET /users
    public Response getAllUsers() {
        ArrayList<User> users;
        try {
            users = getUserDao().readAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
        if(users.isEmpty()){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No Users found\", \"data\": null }"
            );
        }
        /*StringBuilder userData = new StringBuilder();
        for(User user : users){
            userData.append(user.showUserData());
        }*/
        try {
            String userDataJSON = getObjectMapper().writeValueAsString(users);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + userDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
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
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Username or password not set\", \"data\": null }"
            );
        }

        try{
            User user = new User(username, password);
            User createdUser = getUserDao().create(user);
            String userDataJSON = getObjectMapper().writeValueAsString(createdUser);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + userDataJSON + ", \"error\": null }"
            );

        }catch(JsonProcessingException | SQLException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
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
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Username or password not set\", \"data\": null }"
            );
        }

        User user = null;
        try {
            user = getUserDao().read(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
        if (user == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No User with this name\", \"data\": null }"
            );
        }
        if(!Objects.equals(user.getPassword(), password)) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"Incorrect password\", \"data\": null }"
            );
        }
        String token = user.getUsername() + "-mtcgToken";
        //push token to db?
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + user.getUsername() + " logged in" + ", \"error\": null }"
        );
    }

    //DELETE /users/username
    public Response deleteUser(String username) {
        User userToDelete = null;
        try {
            userToDelete = getUserDao().read(username);
            if (userToDelete == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"No User with this name\", \"data\": null }"
                );
            }
            getUserDao().delete(userToDelete);
        }catch (SQLException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + userToDelete.getUsername() + " deleted" + ", \"error\": null }"
        );
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
        User oldUser;
        User newUser;
        //beg. bei 1 und dann +4 sind die deskriptoren
        try {
            oldUser = getUserDao().read(username);

            //serModel oldUser = getUserService().getUserByID(id);
            if (oldUser == null) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"No User with this name\", \"data\": null }"
                );
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
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + newUser.getUsername() + "was updated \", \"error\": null }"
        );

    }
}
