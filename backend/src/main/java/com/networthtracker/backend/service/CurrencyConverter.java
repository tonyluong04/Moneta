package com.networthtracker.backend.service;

import com.networthtracker.backend.entity.FxRate;
import com.networthtracker.backend.entity.FxRateId;
import com.networthtracker.backend.repository.FxRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * The only code that applies an exchange rate (PRD §4.5.1). Services call it;
 * they never multiply by a rate themselves.
 *
 * Every stored rate is "units per 1 USD", so any pair goes through USD:
 * amount × rate(USD→to) ÷ rate(USD→from). The division runs at scale 12,
 * HALF_EVEN, and the result is NOT rounded to a currency's minor unit — callers
 * round once, on the final figure (PRD §4.5.3).
 */
@Service
public class CurrencyConverter {

    private static final int SCALE = 12;

    private final FxRateRepository fxRateRepository;

    public CurrencyConverter(FxRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }

    /** Convert at the newest stored rates — "what is this worth now". */
    public BigDecimal convert(BigDecimal amount, String from, String to) {
        if (from.equals(to)) {
            return amount;
        }
        LocalDate setDate = fxRateRepository.findLatestRateDate()
                .orElseThrow(CurrencyConverter::noRatesStored);
        return convertWithSet(amount, from, to, setDate);
    }

    /**
     * Convert at the rates of a given day — "what was this worth then" (Phase 6
     * snapshots). Uses the newest set on or before {@code date}; a date before all
     * stored history falls back to the oldest set, since history only starts the
     * day rates were first fetched.
     */
    public BigDecimal convert(BigDecimal amount, String from, String to, LocalDate date) {
        if (from.equals(to)) {
            return amount;
        }
        LocalDate setDate = fxRateRepository.findLatestRateDateOnOrBefore(date)
                .or(fxRateRepository::findOldestRateDate)
                .orElseThrow(CurrencyConverter::noRatesStored);
        return convertWithSet(amount, from, to, setDate);
    }

    private BigDecimal convertWithSet(BigDecimal amount, String from, String to, LocalDate setDate) {
        return amount.multiply(rateOf(to, setDate))
                .divide(rateOf(from, setDate), SCALE, RoundingMode.HALF_EVEN);
    }

    private BigDecimal rateOf(String code, LocalDate setDate) {
        return fxRateRepository.findById(new FxRateId(code, setDate))
                .map(FxRate::getRate)
                .orElseThrow(() -> new IllegalArgumentException("No exchange rate for " + code));
    }

    private static IllegalArgumentException noRatesStored() {
        return new IllegalArgumentException("No exchange rates stored yet");
    }
}
