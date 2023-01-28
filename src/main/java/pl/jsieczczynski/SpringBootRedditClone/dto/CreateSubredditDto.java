package pl.jsieczczynski.SpringBootRedditClone.dto;

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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubredditDto {
    @Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters")
    @Pattern(regexp = "[^/]*", message = "String should not contain /")
    @Unique(service = SubredditService.class, fieldName = "name", message = "Name is already taken")
    private String name;
    @NotEmpty(message = "Description is required")
    private String description;
    @URL(message = "Image URL must be valid URL")
    private String imageUrl;
    @URL(message = "Banner URL must be valid URL")
    private String bannerUrl;
}
