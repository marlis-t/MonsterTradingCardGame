import app.App;
import app.server.Server;
import app.services.DatabaseService;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        App app = null;
        try {
            app = new App(new DatabaseService().getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        };
        Server server = new Server(app, 7777);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 