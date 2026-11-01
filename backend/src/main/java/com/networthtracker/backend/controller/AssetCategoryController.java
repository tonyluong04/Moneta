package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.AssetCategoryResponse;
import com.networthtracker.backend.service.AssetCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// the /assets/categories endpoint (login required). kept apart from AssetController
// the way CategoryController is kept apart from BudgetController.
// read-only: the category list is seeded by migration, not managed by users.
@RestController
@RequestMapping("/api/v1/assets/categories")
public class AssetCategoryController {

    private final AssetCategoryService assetCategoryService;

    public AssetCategoryController(AssetCategoryService assetCategoryService) {
        this.assetCategoryService = assetCategoryService;
    }

    // GET /assets/categories — the full list, in display order
    @GetMapping
    public ResponseEntity<List<AssetCategoryResponse>> getCategories() {
        return ResponseEntity.ok(assetCategoryService.findAll());
    }
}
