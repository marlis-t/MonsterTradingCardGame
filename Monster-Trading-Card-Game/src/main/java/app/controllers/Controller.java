package app.controllers;

import app.daos.*;
import app.http.ContentType;
import app.http.HttpStatus;
import app.server.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
@Getter
@Setter
public class Controller {

    private ObjectMapper objectMapper;
    private UserDao userDao;


    public Controller(UserDao userDao) {
        setObjectMapper(new ObjectMapper());
        setUserDao(userDao);
    }

    public Boolean isAuthorized(String authToken) throws SQLException {
        ArrayList<String> auths;
        auths = getUserDao().readAuthToken();
        return auths.contains(authToken);
    }
    public Response sendResponse(String data, String error, HttpStatus status){
        if(status == null){
            throw new IllegalArgumentException("Status cannot be null");
        }
        return new Response(
                status,
                ContentType.JSON,
                "{ \"data\": \"" + data + "\", \"error\": " + error + " }"
        );
    }
    public Response sendResponseWithType(String data, String error, HttpStatus status, ContentType type){
        if(status == null || type == null){
            throw new IllegalArgumentException("Status and ContentType cannot be null");
        }
        return new Response(
                status,
                type,
                "{ \"data\": \"" + data + "\", \"error\": " + error + " }"
        );
    }
}
