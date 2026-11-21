package com.networthtracker.backend.controller;

import com.networthtracker.backend.dto.CurrenciesResponse;
import com.networthtracker.backend.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// the /currencies endpoint (login required).
@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    // GET /currencies — the display list and the latest rates' as-of date (200 OK)
    @GetMapping
    public ResponseEntity<CurrenciesResponse> getCurrencies() {
        return ResponseEntity.ok(currencyService.buildCurrenciesResponse());
    }
}
