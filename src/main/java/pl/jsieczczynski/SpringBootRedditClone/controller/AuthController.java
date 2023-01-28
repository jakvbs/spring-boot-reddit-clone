package pl.jsieczczynski.SpringBootRedditClone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jsieczczynski.SpringBootRedditClone.dto.AuthResponse;
import pl.jsieczczynski.SpringBootRedditClone.dto.SigninRequest;
import pl.jsieczczynski.SpringBootRedditClone.dto.SignupRequest;
import pl.jsieczczynski.SpringBootRedditClone.service.AuthService;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequest body
    ) {
        authService.signup(body);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/signup/confirm")
    public ResponseEntity<String> confirm(@PathParam("token") String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(
            @Valid @RequestBody SigninRequest body
    ) {
        return ResponseEntity.ok(authService.signin(body));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh() {
        return ResponseEntity.ok(authService.refresh());
    }
}
