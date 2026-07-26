package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// database access for users. spring writes the SQL from the method names.
public interface UserRepository extends JpaRepository<User, Integer> {

    // look up a user by email (used at login)
    Optional<User> findByEmail(String email);

    // look up a user by username
    Optional<User> findByUsername(String username);

    // true if an email is already taken (used at register)
    boolean existsByEmail(String email);

    // true if a username is already taken (used at register)
    boolean existsByUsername(String username);
}
