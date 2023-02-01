package pl.jsieczczynski.SpringBootRedditClone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String title;
    private String body;
    private long numberOfComments;
    private long voteScore;
    private long userVote;
    private String username;
    private String subredditName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Body is required")
        private String body;

        @NotBlank(message = "Subreddit name is required")
        private String subredditName;
    }
}
