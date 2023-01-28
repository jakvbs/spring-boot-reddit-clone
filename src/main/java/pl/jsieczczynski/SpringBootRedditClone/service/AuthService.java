package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.dto.AuthResponse;
import pl.jsieczczynski.SpringBootRedditClone.dto.SigninRequest;
import pl.jsieczczynski.SpringBootRedditClone.dto.SignupRequest;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.SpringRedditException;
import pl.jsieczczynski.SpringBootRedditClone.model.*;
import pl.jsieczczynski.SpringBootRedditClone.repository.RefreshTokenRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.UserRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.VerificationTokenRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    @Value("${app.host}")
    private String host;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public void signup(SignupRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String token = generateVerificationToken(user);
        NotificationEmail notificationEmail = NotificationEmail.builder()
                .subject("Please Activate your Account")
                .recipient(user.getEmail())
                .body("Thank you for signing up to Spring Boot Reddit Clone, " +
                        "please click on the below url to activate your account: " +
                        host + "/api/auth/signup/confirm?token=" + token)
                .build();
        mailService.sendMail(notificationEmail);
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token")));
        verificationTokenRepository.deleteByToken(token);
    }

    public AuthResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new SpringRedditException("User not found"));
        String jwtToken = accessTokenService.generateToken(user);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtToken);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserUsername(user.getUsername());
        if (refreshToken.isEmpty()) {
            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setUser(user);
            newRefreshToken.setToken(refreshTokenService.generateToken(user));
            refreshTokenRepository.save(newRefreshToken);
            response.setRefreshToken(newRefreshToken.getToken());
        } else {
            response.setRefreshToken(refreshToken.get().getToken());
        }
        return response;
    }

    public void logout() {
        User user = getCurrentUserOrThrow();
        refreshTokenRepository.deleteByUserUsername(user.getUsername());
    }

    public AuthResponse refresh() {
        User user = getCurrentUserOrThrow();
        RefreshToken refreshToken = refreshTokenRepository.findByUserUsername(user.getUsername())
                .orElseThrow(() -> new SpringRedditException("Refresh token not found"));
        String newToken = accessTokenService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(newToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    User getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            var principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return userRepository.findByUsername(((User) principal).getUsername()).orElseThrow(() -> new SpringRedditException("User not found"));
            }
        }
        throw new SpringRedditException("User not found");
    }
}
