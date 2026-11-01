package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.AssetCategoryResponse;
import com.networthtracker.backend.dto.CreateAssetCategoryRequest;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.AssetCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// the /assets/categories endpoints (login required). kept apart from AssetController
// the way CategoryController is kept apart from BudgetController.
@RestController
@RequestMapping("/api/v1/assets/categories")
public class AssetCategoryController {

    private final AssetCategoryService assetCategoryService;

    public AssetCategoryController(AssetCategoryService assetCategoryService) {
        this.assetCategoryService = assetCategoryService;
    }

    // GET /assets/categories — the presets plus this user's own (200 OK)
    @GetMapping
    public ResponseEntity<List<AssetCategoryResponse>> getCategories(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(assetCategoryService.findVisible(user));
    }

    // POST /assets/categories — add a category of your own (201 Created)
    @PostMapping
    public ResponseEntity<AssetCategoryResponse> createCategory(
            Authentication authentication,
            @Valid @RequestBody CreateAssetCategoryRequest request) {
        User user = (User) authentication.getPrincipal();
        AssetCategoryResponse created = assetCategoryService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE /assets/categories/{id} — your own, and only when empty (204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        assetCategoryService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
