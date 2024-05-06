package exceptions;

import lombok.Getter;

@Getter
public class APIException extends RuntimeException {
    private int statusCode;
    private String timeStamp;

    public APIException(int statusCode, String message, String timeStamp) {
        super(message);
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
    }
}
