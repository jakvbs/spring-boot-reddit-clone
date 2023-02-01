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
import pl.jsieczczynski.SpringBootRedditClone.dto.request.SigninRequest;
import pl.jsieczczynski.SpringBootRedditClone.dto.request.SignupRequest;
import pl.jsieczczynski.SpringBootRedditClone.dto.response.AuthResponse;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.AppException;
import pl.jsieczczynski.SpringBootRedditClone.model.NotificationEmail;
import pl.jsieczczynski.SpringBootRedditClone.model.RefreshToken;
import pl.jsieczczynski.SpringBootRedditClone.model.User;
import pl.jsieczczynski.SpringBootRedditClone.model.VerificationToken;
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
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
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
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new AppException("Invalid Token")));
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
                .orElseThrow(() -> new AppException("User not found"));
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
        User user = getCurrentUser().orElseThrow(() -> new AppException("User not found"));
        refreshTokenRepository.deleteByUserUsername(user.getUsername());
    }

    public AuthResponse refresh() {
        User user = getCurrentUser().orElseThrow(() -> new AppException("User not found"));
        RefreshToken refreshToken = refreshTokenRepository.findByUserUsername(user.getUsername())
                .orElseThrow(() -> new AppException("Refresh token not found"));
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

    Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            var principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                return userRepository.findByUsername(user.getUsername());
            }
        }
        return Optional.empty();
    }
}
