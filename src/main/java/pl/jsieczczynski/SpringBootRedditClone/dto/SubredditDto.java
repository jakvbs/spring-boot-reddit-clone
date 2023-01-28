package pl.jsieczczynski.SpringBootRedditClone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubredditDto {
    private Long id;
    private Instant createdAt;
    private String name;
    private String description;
    private String imageUrl;
    private String bannerUrl;
    private Integer numberOfPosts;
    private Integer numberOfUsers;
    private String author;
}
