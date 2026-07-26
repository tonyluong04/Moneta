package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.AuthResponse;
import com.networthtracker.backend.dto.LoginRequest;
import com.networthtracker.backend.dto.RegisterRequest;
import com.networthtracker.backend.entity.RefreshToken;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.RefreshTokenRepository;
import com.networthtracker.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// the auth brains: register, login, refresh, and token creation.
@Service
public class AuthService {

    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // create a new account, then hand out tokens
    @Transactional
    public AuthResult register(RegisterRequest request) {
        // reject if email or username is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // hash the password before saving — never store plain text
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        return generateTokens(user);
    }

    // check email + password, then hand out tokens
    @Transactional
    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // compare the submitted password against the stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Revoke existing refresh tokens for this user
        refreshTokenRepository.deleteByUser(user);

        return generateTokens(user);
    }

    // swap a valid refresh token for a fresh pair of tokens
    @Transactional
    public AuthResult refresh(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new IllegalArgumentException("Refresh token is missing");
        }

        // the token must exist in the DB
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // ...and not be past its 7-day expiry
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        // Rotate: delete old token, create new one (each token is single-use)
        refreshTokenRepository.delete(refreshToken);

        return generateTokens(user);
    }

    // make a new access token + a new refresh token row for the user
    private AuthResult generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS));
        refreshTokenRepository.save(refreshToken);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return new AuthResult(response, refreshToken.getToken());
    }

    /**
     * Bundles the API response body with the raw refresh token value
     * so the controller can set it as an HttpOnly cookie.
     */
    public record AuthResult(AuthResponse response, String refreshToken) {}
}
