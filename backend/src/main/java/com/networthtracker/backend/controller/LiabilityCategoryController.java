package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.LiabilityCategoryResponse;
import com.networthtracker.backend.service.LiabilityCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// the /liabilities/categories endpoint (login required).
// read-only, same as /assets/categories and /categories.
@RestController
@RequestMapping("/api/v1/liabilities/categories")
public class LiabilityCategoryController {

    private final LiabilityCategoryService liabilityCategoryService;

    public LiabilityCategoryController(LiabilityCategoryService liabilityCategoryService) {
        this.liabilityCategoryService = liabilityCategoryService;
    }

    // GET /liabilities/categories — the full list, in display order
    @GetMapping
    public ResponseEntity<List<LiabilityCategoryResponse>> getCategories() {
        return ResponseEntity.ok(liabilityCategoryService.findAll());
    }
}
