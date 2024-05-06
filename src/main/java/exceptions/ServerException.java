package exceptions;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private int statusCode;
    private String timeStamp;

    public ServerException(int statusCode, String message, String timeStamp) {
        super(message);
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
    }
}
