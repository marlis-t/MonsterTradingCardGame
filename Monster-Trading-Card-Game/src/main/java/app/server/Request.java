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
            String line;
            line = inputStream.readLine();

            //if content added
            if (line != null) {
                String[] splitFirstLine = line.split(" ");
                Boolean hasParams = splitFirstLine[1].contains("?");

                setMethod(getMethodFromInputLine(splitFirstLine));
                setPathname(getPathnameFromInputLine(splitFirstLine, hasParams));
                setParameters(getParamsFromInputLine(splitFirstLine, hasParams));

                while (!line.isEmpty()) {
                    line = inputStream.readLine();
                    if (line.startsWith(CONTENT_LENGTH)) {
                        setContentLength(getContentLengthFromInputLine(line));
                    }
                    if (line.startsWith(CONTENT_TYPE)) {
                        setContentType(getContentTypeFromInputLine(line));
                    }//added to parse token from header
                    if(line.startsWith("Authorization")){
                        setAuthToken(getAuthorizationFromInputLine(line));
                    }
                }

                if (getMethod() == Method.POST || getMethod() == Method.PUT) {
                    if(getContentLength() != null){
                        int asciChar;
                        for (int i = 0; i < getContentLength(); i++) {
                            asciChar = inputStream.read();
                            String body = getBody();
                            setBody(body + ((char) asciChar));
                        }
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

    private String getAuthorizationFromInputLine(String line){
        String[] splitLine = line.split(" ");
        String auth = "";
        for(int i = 0; i < Arrays.stream(splitLine).count(); i++){
            if(splitLine[i].equals("Bearer")){
                auth = splitLine[i+1];
            }
        }
        return auth;
    }
}

