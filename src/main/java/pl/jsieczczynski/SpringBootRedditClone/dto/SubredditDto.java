package pl.jsieczczynski.SpringBootRedditClone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubredditDto {
    private Long id;
    private Date createdAt;
    @Size(max = 32, message = "Name must be less than 32 characters")
    @Pattern(regexp = "[^/]+", message = "String should not contain /")
    private String name;
    @NotEmpty(message = "Description is required")
    private String description;
    @URL(message = "Image URL must be valid URL")
    private String imageUrl;
    @URL(message = "Banner URL must be valid URL")
    private String bannerUrl;
    private Integer numberOfPosts;
    private Integer numberOfUsers;
}
