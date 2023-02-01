package pl.jsieczczynski.SpringBootRedditClone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import pl.jsieczczynski.SpringBootRedditClone.service.UserService;
import pl.jsieczczynski.SpringBootRedditClone.validators.exists.Exists;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String imageUrl;
    private String about;
    private boolean enabled;
    private boolean locked;
    private long numberOfCreatedSubreddits;
    private long numberOfJoinedSubreddits;
    private long numberOfPosts;
    private long numberOfComments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Add {
        @Exists(service = UserService.class, fieldName = "username", message = "User does not exist")
        private String username;

        @NotBlank(message = "Subreddit name is required")
        private String subredditName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        @NotBlank(message = "Username is required")
        private String username;

//        @Email(message = "Email is not valid")
//        @NotBlank(message = "Email is required")
//        @Unique(service = UserService.class, fieldName = "email", message = "Email is already taken")
//        private String email;

//        @NotBlank(message = "Password is required")
//        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one digit and one special character")
//        private String password;

        @URL(message = "Image URL is not valid")
        private String imageUrl;

        private String about;
    }
}
