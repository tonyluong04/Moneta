package com.networthtracker.backend.dto;

import java.time.LocalDateTime;

// the safe profile info we send back (no password).
public class UserProfileResponse {

    private String username;
    private String email;
    private String preferredCurrency;
    private LocalDateTime createdAt;

    public UserProfileResponse() {}

    public UserProfileResponse(String username, String email, String preferredCurrency, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.preferredCurrency = preferredCurrency;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
