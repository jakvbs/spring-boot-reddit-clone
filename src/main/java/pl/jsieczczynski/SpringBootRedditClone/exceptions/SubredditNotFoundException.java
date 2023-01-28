package pl.jsieczczynski.SpringBootRedditClone.exceptions;

public class SubredditNotFoundException extends InvalidRequestException {
    public SubredditNotFoundException(String message) {
        super(message);
    }
}
