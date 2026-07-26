package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.BudgetEntryResponse;
import com.networthtracker.backend.dto.CreateBudgetEntryRequest;
import com.networthtracker.backend.dto.UpdateBudgetEntryRequest;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.BudgetEntryService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// the /budget endpoints (login required).
@RestController
@RequestMapping("/api/v1/budget")
public class BudgetController {

    private final BudgetEntryService budgetEntryService;

    public BudgetController(BudgetEntryService budgetEntryService) {
        this.budgetEntryService = budgetEntryService;
    }

    // GET /budget?month=2026-07-01 — all rows for that month (200 OK)
    @GetMapping
    public ResponseEntity<List<BudgetEntryResponse>> getBudget(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(budgetEntryService.findByMonth(user, month));
    }    

    // POST /budget — create a budget row (201 Created)
    @PostMapping
    public ResponseEntity<BudgetEntryResponse> createBudgetEntry(
            Authentication authentication,
            @Valid @RequestBody CreateBudgetEntryRequest request) {
        User user = (User) authentication.getPrincipal();
        BudgetEntryResponse created = budgetEntryService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }    

    // PUT /budget/{id} — update amounts/notes (mainly entering actuals) (200 OK)
    @PutMapping("/{id}")
    public ResponseEntity<BudgetEntryResponse> updateBudgetEntry(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateBudgetEntryRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(budgetEntryService.update(user, id, request));
    }

    // DELETE /budget/{id} — remove the user's own row (204 No Content) 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudgetEntry(
            Authentication authentication,
            @PathVariable Integer id) {
        User user = (User) authentication.getPrincipal();
        budgetEntryService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    // POST /budget/copy-forward?month=2026-07-01 — copy last month's plan into this month (201 CREATED)
    @PostMapping("/copy-forward")
    public ResponseEntity<List<BudgetEntryResponse>> copyForward(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        User user = (User) authentication.getPrincipal();
        List<BudgetEntryResponse> created = budgetEntryService.copyForward(user, month);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // turn a bad-input exception into a clean 400 response
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
}
