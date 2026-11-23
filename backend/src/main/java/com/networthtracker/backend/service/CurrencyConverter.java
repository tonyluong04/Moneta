package com.networthtracker.backend.service;

import com.networthtracker.backend.entity.FxRate;
import com.networthtracker.backend.entity.FxRateId;
import com.networthtracker.backend.repository.FxRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Currency;

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

    /**
     * Round a final figure to the currency's own minor unit (2 for AUD, 0 for JPY),
     * HALF_EVEN. Call it once, on the figure that is shown — a row's display value
     * or a total — never on the parts a total is summed from (PRD §4.5.3).
     */
    public BigDecimal round(BigDecimal amount, String currency) {
        int digits = Math.max(Currency.getInstance(currency).getDefaultFractionDigits(), 0);
        return amount.setScale(digits, RoundingMode.HALF_EVEN);
    }

    /**
     * Convert one amount at the newest rates and round it — for a figure that is
     * shown on its own, like one row's display value. Never sum these: a total is
     * built from {@link #convert} results and rounded once at the end.
     */
    public BigDecimal convertAndRound(BigDecimal amount, String from, String to) {
        return round(convert(amount, from, to), to);
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
