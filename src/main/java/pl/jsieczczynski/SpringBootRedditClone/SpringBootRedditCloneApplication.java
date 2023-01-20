package pl.jsieczczynski.SpringBootRedditClone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.jsieczczynski.SpringBootRedditClone.auth.AuthService;
import pl.jsieczczynski.SpringBootRedditClone.auth.RegisterRequest;
import pl.jsieczczynski.SpringBootRedditClone.model.Role;
import pl.jsieczczynski.SpringBootRedditClone.model.User;
import pl.jsieczczynski.SpringBootRedditClone.repository.UserRepository;

@SpringBootApplication
public class SpringBootRedditCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedditCloneApplication.class, args);
    }

    @Bean
    public ApplicationRunner appSetup(@Autowired AuthService authService,
                                      @Autowired UserRepository userRepository,
                                      @Autowired PasswordEncoder passwordEncoder
    ) {
        return args -> {
            RegisterRequest userRequest = RegisterRequest.builder()
                    .username("user")
                    .email("user@example.com")
                    .password("password")
                    .build();
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            authService.register(userRequest);
            userRepository.save(admin);
            System.out.println(passwordEncoder.matches("password", admin.getPassword()));
            System.out.println(passwordEncoder.matches("password", admin.getPassword()));
            System.out.println("User and admin accounts created");
            System.out.println("Application started");
        };
    }

}
