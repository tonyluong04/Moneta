package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.CategoryResponse;
import com.networthtracker.backend.dto.CreateCategoryRequest;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// the /categories endpoints (login required).
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /categories — list presets + the user's own
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(categoryService.findAllForUser(user));
    }

    // POST /categories — create a custom category (201 Created)
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            Authentication authentication,
            @Valid @RequestBody CreateCategoryRequest request) {
        User user = (User) authentication.getPrincipal();
        CategoryResponse created = categoryService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE /categories/{id} — remove the user's own category (204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        categoryService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    // turn a bad-input exception into a clean 400 response
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
}
