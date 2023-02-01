package pl.jsieczczynski.SpringBootRedditClone.dto.model;

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
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Body is required")
    private String body;
    private long numberOfComments;
    private long voteScore;
    private long userVote;
    private String username;
    private String subredditName;
}
