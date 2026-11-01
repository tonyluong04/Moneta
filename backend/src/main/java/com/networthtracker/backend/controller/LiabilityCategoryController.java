package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.CreateLiabilityCategoryRequest;
import com.networthtracker.backend.dto.LiabilityCategoryResponse;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.LiabilityCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// the /liabilities/categories endpoints (login required).
@RestController
@RequestMapping("/api/v1/liabilities/categories")
public class LiabilityCategoryController {

    private final LiabilityCategoryService liabilityCategoryService;

    public LiabilityCategoryController(LiabilityCategoryService liabilityCategoryService) {
        this.liabilityCategoryService = liabilityCategoryService;
    }

    // GET /liabilities/categories — the presets plus this user's own (200 OK)
    @GetMapping
    public ResponseEntity<List<LiabilityCategoryResponse>> getCategories(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(liabilityCategoryService.findVisible(user));
    }

    // POST /liabilities/categories — add a category of your own (201 Created)
    @PostMapping
    public ResponseEntity<LiabilityCategoryResponse> createCategory(
            Authentication authentication,
            @Valid @RequestBody CreateLiabilityCategoryRequest request) {
        User user = (User) authentication.getPrincipal();
        LiabilityCategoryResponse created = liabilityCategoryService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE /liabilities/categories/{id} — your own, and only when empty (204)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        liabilityCategoryService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
