package pl.jsieczczynski.SpringBootRedditClone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.jsieczczynski.SpringBootRedditClone.service.UserService;
import pl.jsieczczynski.SpringBootRedditClone.validators.unique.Unique;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
    private String password;
}
