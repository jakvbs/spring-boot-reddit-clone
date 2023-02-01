package pl.jsieczczynski.SpringBootRedditClone.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import pl.jsieczczynski.SpringBootRedditClone.service.SubredditService;
import pl.jsieczczynski.SpringBootRedditClone.validators.unique.Unique;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubredditDto {
    private long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String description;
    private String imageUrl;
    private String bannerUrl;
    private long numberOfPosts;
    private long numberOfUsers;
    private String author;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private long id;
        @Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters")
        @Unique(service = SubredditService.class, fieldName = "name", message = "Name is already taken")
        @Pattern(regexp = "[^/]*", message = "String should not contain /")
        private String name;
        @NotEmpty(message = "Description is required")
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private long id;
        private String name;
        @NotEmpty(message = "Description is required")
        private String description;
        @URL(message = "Image URL must be valid URL")
        private String imageUrl;
        @URL(message = "Banner URL must be valid URL")
        private String bannerUrl;
    }
}
