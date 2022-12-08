import app.App;
import app.server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        App app = new App();
        Server server = new Server(app, 7777);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //SpellFight also
        //curl
        //add monster types to enum + max. damages
        //change all models to actual classes
        //login function sets logged-in and security token to user and environment
        //login function initializes user with all needed variables
        //change createCard to use CardGenerator
        //change all methods to use ID of currently logged-in user instead of taking it from request
        //add battling

        //add method that configures deck
        //add method that shows deck
        //add method that shows user score, name, games won and games lost
        //add method that shows scoreboard (user score, name, games won and games lost of both? users)
        //add method to go to "lobby", wait for other user to enter, then start battle
        //add method to do trade (offer accepted, demand met, /trades/userID
    }
} 