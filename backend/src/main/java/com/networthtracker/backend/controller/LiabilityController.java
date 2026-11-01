package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.CreateLiabilityRequest;
import com.networthtracker.backend.dto.LiabilityResponse;
import com.networthtracker.backend.dto.UpdateLiabilityRequest;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.LiabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// the /liabilities endpoints (login required). same shape as AssetController —
// a separate stack rather than a shared one, because the two tables differ in
// everything except their CRUD verbs.
@RestController
@RequestMapping("/api/v1/liabilities")
public class LiabilityController {

    private final LiabilityService liabilityService;

    public LiabilityController(LiabilityService liabilityService) {
        this.liabilityService = liabilityService;
    }

    // GET /liabilities — everything the signed-in user owes (200 OK)
    @GetMapping
    public ResponseEntity<List<LiabilityResponse>> getLiabilities(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(liabilityService.findAll(user));
    }

    // categories live in LiabilityCategoryController, at /liabilities/categories

    // GET /liabilities/{id} — one liability (200 OK)
    @GetMapping("/{id}")
    public ResponseEntity<LiabilityResponse> getLiability(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(liabilityService.findOne(user, id));
    }

    // POST /liabilities — add a debt (201 Created)
    @PostMapping
    public ResponseEntity<LiabilityResponse> createLiability(
            Authentication authentication,
            @Valid @RequestBody CreateLiabilityRequest request) {
        User user = (User) authentication.getPrincipal();
        LiabilityResponse created = liabilityService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /liabilities/{id} — change any subset of the fields (200 OK)
    @PutMapping("/{id}")
    public ResponseEntity<LiabilityResponse> updateLiability(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateLiabilityRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(liabilityService.update(user, id, request));
    }

    // DELETE /liabilities/{id} — remove the user's own debt (204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiability(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        liabilityService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
