package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.AssetResponse;
import com.networthtracker.backend.dto.CategoryOption;
import com.networthtracker.backend.dto.CreateAssetRequest;
import com.networthtracker.backend.dto.UpdateAssetRequest;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// the /assets endpoints (login required).
@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // GET /assets — every asset the signed-in user owns, in card order (200 OK)
    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAssets(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(assetService.findAll(user));
    }

    // GET /assets/categories — the 8 categories for the add form and the cards (200 OK).
    // declared above /{id} for readability; spring matches the literal path first either
    // way, and "categories" would never convert to an Integer id regardless.
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryOption>> getCategories() {
        return ResponseEntity.ok(assetService.findCategories());
    }

    // GET /assets/{id} — one asset, 400 if it is not this user's (200 OK)
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAsset(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(assetService.findOne(user, id));
    }

    // POST /assets — add an asset (201 Created)
    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(
            Authentication authentication,
            @Valid @RequestBody CreateAssetRequest request) {
        User user = (User) authentication.getPrincipal();
        AssetResponse created = assetService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /assets/{id} — change any subset of the fields (200 OK)
    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAssetRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(assetService.update(user, id, request));
    }

    // DELETE /assets/{id} — remove the user's own asset (204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        assetService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
