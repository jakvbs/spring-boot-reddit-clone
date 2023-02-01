package pl.jsieczczynski.SpringBootRedditClone.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.jsieczczynski.SpringBootRedditClone.service.UserService;
import pl.jsieczczynski.SpringBootRedditClone.validators.unique.Unique;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Username is required")
    @Unique(service = UserService.class, fieldName = "username", message = "Username is already taken")
    private String username;

    @Email(message = "Email is not valid")
    @NotBlank(message = "Email is required")
    @Unique(service = UserService.class, fieldName = "email", message = "Email is already taken")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one digit and one special character")
    private String password;
}
