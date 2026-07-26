package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.RefreshToken;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

// database access for refresh tokens.
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    // find a token row by its string value (used at refresh)
    Optional<RefreshToken> findByToken(String token);

    // wipe all of a user's tokens (used at login to revoke old sessions)
    @Transactional
    void deleteByUser(User user);

    // clean up tokens that expired before a given time
    @Transactional
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
