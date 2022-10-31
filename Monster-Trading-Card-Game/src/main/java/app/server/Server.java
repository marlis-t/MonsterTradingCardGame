package app.server;

import app.App;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Server {
    private ServerSocket serverSocket;//
    private App app;//
    private int port;//

    public Server(App app, int port) {
        setApp(app);
        setPort(port);
    }
    public void start() throws IOException {
        setServerSocket(new ServerSocket(getPort()));
        run();
    }
    public void run(){
        while(true){
            try{
                Socket clientSocket = getServerSocket().accept();
                Thread thread = new Thread(new RequestHandler(getApp(), clientSocket));
                thread.start();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
