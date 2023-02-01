package pl.jsieczczynski.SpringBootRedditClone.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;
    private Instant createdAt;
    private Instant updatedAt;
    @NotEmpty(message = "Comment body is required")
    private String body;
    private String author;
    private int voteScore;
    private int userVote;
    private long postId;
}
