package app.controllers;

import app.http.ContentType;
import app.http.HttpStatus;
import app.models.UserModel;
import app.server.Response;
import app.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserController extends Controller{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private UserService userService;

    public UserController(UserService userService) {
        setUserService(userService);
    }
    //GET /user/id
    public Response getUserById(int id) {
        try{
            UserModel user = getUserService().getUserByID(id);
            if(user == null){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"No User with this ID\", \"data\": null }"
                );
            }
            String userDataJSON = getObjectMapper().writeValueAsString(user);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + userDataJSON + ", \"error\": null }"
            );
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }
    //GET /users/id
    public Response getAllUsersExceptSelf(int Uid) {
        try {
            List userData = getUserService().getAllUsersExceptSelf(Uid);
            String userDataJSON = getObjectMapper().writeValueAsString(userData);

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
    //GET /user/username/password/
    public Response getUserByCredentials(String username, String password){
        try{
            UserModel userExists = getUserService().checkIfUserExists(username, password);
            String userDataJSON = getObjectMapper().writeValueAsString(userExists);
            if(userExists == null){
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"data\": User does not exist , \"error\": null }"
                );
            }else{
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"data\": " + userDataJSON + " , \"error\": null }"
                );
            }
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }
    //POST /user
    public Response createUser(String body) {
        try{
            UserModel user = getObjectMapper().readValue(body, UserModel.class);
            getUserService().registerUser(user);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + user + ", \"error\": null }"
            );

        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }
    }
    //DELETE /user/id
    public Response deleteUser(int id) {
        UserModel user = getUserService().getUserByID(id);
        if(user == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No User with this ID\", \"data\": null }"
            );
        }
        String userName = user.getUsername();
        getUserService().deleteUser(id);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"data\": " + userName + " deleted" + ", \"error\": null }"
        );
    }
    //PUT /user/id
    public Response updateUser(int id, String body){
        UserModel oldUser = getUserService().getUserByID(id);
        if(oldUser == null){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No User with this ID\", \"data\": null }"
            );
        }
        try{
            UserModel user = getObjectMapper().readValue(body, UserModel.class);
            getUserService().updateUser(id, user);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + user + ", \"error\": null }"
            );
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Internal Server Error\", \"data\": null }"
            );
        }

    }
}
