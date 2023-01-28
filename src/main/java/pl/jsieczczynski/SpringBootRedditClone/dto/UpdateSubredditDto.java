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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubredditDto {
    Long id;
    private String name;
    @NotEmpty(message = "Description is required")
    private String description;
    @URL(message = "Image URL must be valid URL")
    private String imageUrl;
    @URL(message = "Banner URL must be valid URL")
    private String bannerUrl;
}
