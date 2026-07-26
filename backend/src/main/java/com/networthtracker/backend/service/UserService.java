package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.UpdateProfileRequest;
import com.networthtracker.backend.dto.UserProfileResponse;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// business logic for the user profile.
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // read the current user's profile
    public UserProfileResponse getProfile(User user) {
        return toProfileResponse(user);
    }

    // update only the fields the user actually sent
    @Transactional
    public UserProfileResponse updateProfile(User user, UpdateProfileRequest request) {
        // username: only change it if it's new and not taken
        if (request.getUsername() != null) {
            if (!request.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(request.getUsername());
        }

        // email: only change it if it's new and not taken
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email is already registered");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPreferredCurrency() != null) {
            user.setPreferredCurrency(request.getPreferredCurrency());
        }

        // password change: require the current password to be correct first
        if (request.getNewPassword() != null) {
            if (request.getCurrentPassword() == null) {
                throw new IllegalArgumentException("Current password is required to set a new password");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        return toProfileResponse(user);
    }

    // convert a User entity into the safe profile DTO (no password)
    private UserProfileResponse toProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPreferredCurrency(user.getPreferredCurrency());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
