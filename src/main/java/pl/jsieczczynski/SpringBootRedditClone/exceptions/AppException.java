package pl.jsieczczynski.SpringBootRedditClone.exceptions;

public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(String message, Exception exception) {
        super(message, exception);
    }
}
