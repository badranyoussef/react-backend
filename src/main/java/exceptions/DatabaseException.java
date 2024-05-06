package exceptions;

import lombok.Getter;

@Getter
public class DatabaseException extends RuntimeException {
    private int statusCode;
    private String timeStamp;

    public DatabaseException(int statusCode, String message, String timeStamp) {
        super(message);
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
    }
}
