package com.networthtracker.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/** The primary key of fx_rates: which currency, on which day. */
@Embeddable
public class FxRateId implements Serializable {

    @Column(name = "quote_currency", nullable = false, length = 3)
    private String quoteCurrency;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    public FxRateId() {}

    public FxRateId(String quoteCurrency, LocalDate rateDate) {
        this.quoteCurrency = quoteCurrency;
        this.rateDate = rateDate;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public LocalDate getRateDate() {
        return rateDate;
    }

    // a composite key must compare by value, or Hibernate treats equal keys as different rows
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof FxRateId that)) return false;
        return Objects.equals(quoteCurrency, that.quoteCurrency)
                && Objects.equals(rateDate, that.rateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quoteCurrency, rateDate);
    }
}
