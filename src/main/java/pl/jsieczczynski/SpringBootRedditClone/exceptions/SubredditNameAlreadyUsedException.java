package pl.jsieczczynski.SpringBootRedditClone.exceptions;

public class SubredditNameAlreadyUsedException extends InvalidRequestException {
    public SubredditNameAlreadyUsedException(String message) {
        super(message);
    }
}
