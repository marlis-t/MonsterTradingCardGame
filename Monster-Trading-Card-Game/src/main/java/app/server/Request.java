package app.server;

import app.http.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

@Getter
@Setter(AccessLevel.PROTECTED)
public class Request {
    private Method method;
    private String pathname;
    private String parameters;
    private String contentType;
    private Integer contentLength;
    private String body = "";
    private String AuthToken;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String CONTENT_TYPE = "Content-Type: ";
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final String CONTENT_LENGTH = "Content-Length: ";

    public Request(BufferedReader inputStream) {
        buildRequest(inputStream);
    }
    private void buildRequest(BufferedReader inputStream) {
        try {
            StringBuilder line = new StringBuilder();
            String temp;

            do{
                temp = inputStream.readLine();
                line.append(temp).append(" ");

            }while(!temp.contains("Authorization") && temp != null);

            //what if line == null
            //if content added
            if (line.toString().length() > 1) {
                String[] splitLine = line.toString().split(" ");
                Boolean hasParams = splitLine[1].contains("?");

                setMethod(getMethodFromInputLine(splitLine));
                setPathname(getPathnameFromInputLine(splitLine, hasParams));
                setParameters(getParamsFromInputLine(splitLine, hasParams));
                setAuthToken(getAuthorizationFromInputLine(splitLine));

                while (line.length() > 0) {
                    line = new StringBuilder(inputStream.readLine());
                    if (line.toString().startsWith(CONTENT_LENGTH)) {
                        setContentLength(getContentLengthFromInputLine(line.toString()));
                    }
                    if (line.toString().startsWith(CONTENT_TYPE)) {
                        setContentType(getContentTypeFromInputLine(line.toString()));
                    }
                }

                if (getMethod() == Method.POST || getMethod() == Method.PUT) {
                    int asciChar;
                    for (int i = 0; i < getContentLength(); i++) {
                        asciChar = inputStream.read();
                        String body = getBody();
                        setBody(body + ((char) asciChar));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Method getMethodFromInputLine(String[] splitFirstLine) {
        return Method.valueOf(splitFirstLine[0].toUpperCase(Locale.ROOT));
    }

    private String getPathnameFromInputLine(String[] splitFirstLine, Boolean hasParams) {
        if (hasParams) {
            return splitFirstLine[1].split("\\?")[0];
        }

        return splitFirstLine[1];
    }


    private String getParamsFromInputLine(String[] splitFirstLine, Boolean hasParams) {
        if (hasParams) {
            return splitFirstLine[1].split("\\?")[1];
        }

        return "";
    }

    private Integer getContentLengthFromInputLine(String line) {
        return Integer.parseInt(line.substring(CONTENT_LENGTH.length()));
    }

    private String getContentTypeFromInputLine(String line) {
        return line.substring(CONTENT_TYPE.length());
    }

    private String getAuthorizationFromInputLine(String[] splittedLine){
        String auth = "";
        for(int i = 0; i < Arrays.stream(splittedLine).count(); i++){
            if(splittedLine[i].equals("Bearer")){
                auth = splittedLine[i+1];
            }
        }
        return auth;
    }
}

