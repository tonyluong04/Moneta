package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.PortfolioSummaryResponse;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// the /portfolio endpoints (login required).
@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // GET /portfolio/summary — hero totals and one total per asset category (200 OK)
    @GetMapping("/summary")
    public ResponseEntity<PortfolioSummaryResponse> getSummary(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(portfolioService.buildSummary(user));
    }
}
