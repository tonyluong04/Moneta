package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.AuthResponse;
import com.networthtracker.backend.dto.LoginRequest;
import com.networthtracker.backend.dto.RegisterRequest;
import com.networthtracker.backend.service.AuthService;
import com.networthtracker.backend.service.AuthService.AuthResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

// the public auth endpoints (no login needed to reach these).
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Duration REFRESH_COOKIE_MAX_AGE = Duration.ofDays(7);
    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /auth/register — create an account
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResult result = authService.register(request);
        return buildAuthResponse(result);
    }

    // POST /auth/login — log in
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request);
        return buildAuthResponse(result);
    }

    // POST /auth/refresh — the refresh token comes from the cookie, not the body
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        AuthResult result = authService.refresh(refreshToken);
        return buildAuthResponse(result);
    }

    // turn a bad-input exception into a clean 400 response
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    // access token goes in the JSON body; refresh token goes in an HttpOnly cookie
    private ResponseEntity<AuthResponse> buildAuthResponse(AuthResult result) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, result.refreshToken())
                .httpOnly(true) // JavaScript can't read it — protects it from theft
                .secure(false) // TODO: set to true in production
                .path("/auth")
                .maxAge(REFRESH_COOKIE_MAX_AGE)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.response());
    }
}
