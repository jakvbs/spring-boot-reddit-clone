package pl.jsieczczynski.SpringBootRedditClone.exceptions;

public abstract class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
