package com.networthtracker.backend.dto;

import java.time.LocalDate;
import java.util.List;

/** GET /api/v1/currencies — what the settings dropdown (7.3) offers, and the rates' as-of date. */
public class CurrenciesResponse {

    /** The configured display currencies, in config order. */
    private List<String> display;

    /** The date of the newest stored rate set; null before the first fetch. */
    private LocalDate ratesAsOf;

    public CurrenciesResponse() {}

    public List<String> getDisplay() {
        return display;
    }

    public void setDisplay(List<String> display) {
        this.display = display;
    }

    public LocalDate getRatesAsOf() {
        return ratesAsOf;
    }

    public void setRatesAsOf(LocalDate ratesAsOf) {
        this.ratesAsOf = ratesAsOf;
    }
}
