package com.networthtracker.backend.entity;

/**
 * Which feed Phase 5 prices a category from. NULL on the category means "valued by
 * hand", which is every category except the two presets below.
 *
 * A closed structural set, so it stays a java enum — same call as CategoryType.
 * The category *list* became a table because users extend it; the set of price
 * feeds only grows when someone writes the code to read one.
 */
public enum PriceSource {

    STOCK,   // 5.1 — Yahoo Finance / Alpha Vantage
    CRYPTO   // 5.2 — CoinGecko
}
