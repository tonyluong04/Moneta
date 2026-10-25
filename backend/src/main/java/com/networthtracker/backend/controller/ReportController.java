package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.BudgetReportResponse;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.BudgetReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// the /reports endpoints (login required).
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final BudgetReportService budgetReportService;

    public ReportController(BudgetReportService budgetReportService) {
        this.budgetReportService = budgetReportService;
    }

    // GET /reports/budget?month=2026-09-01 — everything the dashboard draws
    @GetMapping("/budget")
    public ResponseEntity<BudgetReportResponse> getBudgetReport(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(budgetReportService.buildReport(user, month));
    }
}
