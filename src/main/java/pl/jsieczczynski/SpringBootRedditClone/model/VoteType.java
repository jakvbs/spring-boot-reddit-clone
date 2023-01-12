package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.Getter;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.SpringRedditException;

import java.util.Arrays;

public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1),
    ;

    @Getter
    private int direction;

    VoteType(int direction) {
    }

    public static VoteType lookup(Integer direction) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection() == direction)
                .findAny()
                .orElseThrow(() -> new SpringRedditException("Vote not found"));
    }
}
