package com.networthtracker.backend.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * One exchange rate: how many units of a currency one US dollar buys on a given
 * day. USD itself is stored too, at 1, so lookups need no special case.
 *
 * Persistable, because the id is always assigned: without it Spring Data would
 * treat every new rate as an existing row and merge it, silently overwriting a
 * stored day. With isNew() true it inserts, so a duplicate (quote_currency,
 * rate_date) fails on the primary key — history stays append only.
 */
@Entity
@Table(name = "fx_rates")
public class FxRate implements Persistable<FxRateId> {

    @EmbeddedId
    private FxRateId id;

    @Column(nullable = false, precision = 24, scale = 12)
    private BigDecimal rate;

    @Column(name = "fetched_at", nullable = false, updatable = false)
    private LocalDateTime fetchedAt;

    @Transient
    private boolean isNew = true;

    public FxRate() {}

    public FxRate(FxRateId id, BigDecimal rate) {
        this.id = id;
        this.rate = rate;
    }

    // rate dates are UTC, so the fetch time is too
    @PrePersist
    protected void onCreate() {
        fetchedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    // once saved or loaded the row exists, so a later save would be an update
    @PostPersist
    @PostLoad
    protected void markNotNew() {
        isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public FxRateId getId() {
        return id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }
}
