package app.server;

import app.App;
import app.http.ContentType;
import app.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class RequestHandler implements Runnable{
    private App app;
    private Socket clientSocket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;

    RequestHandler(App app, Socket clientSocket){
        setApp(app);
        setClientSocket(clientSocket);
    }

    public void run(){
        try{
            setInputStream(new BufferedReader(new InputStreamReader(getClientSocket().getInputStream())));
            Request request = new Request(getInputStream());
            setOutputStream(new PrintWriter(getClientSocket().getOutputStream(), true));
            sendResponse(request);

        }catch (IOException e){
            e.printStackTrace();
        }finally{
            closeRequest();
        }
    }
    public void sendResponse(Request request){
        Response response;
        if (request.getPathname() == null) {
            response = new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.TEXT,
                    "Pathname was not set"
            );
        } else {
            response = getApp().handleRequest(request);
        }

        getOutputStream().write(response.build());
        System.out.println("Thread " + Thread.currentThread().getName() + ", Content: " + response.getContent());
    }
    public void closeRequest(){
        try{
            if (getOutputStream() != null) {
                getOutputStream().close();
            }
            if (getInputStream() != null) {
                getInputStream().close();
                getClientSocket().close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
