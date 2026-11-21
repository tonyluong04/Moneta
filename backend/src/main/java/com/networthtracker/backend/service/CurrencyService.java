package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.CurrenciesResponse;
import com.networthtracker.backend.repository.FxRateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Which currencies Moneta offers (PRD §4.5.4). Display currencies — what a
 * user's preferred currency may be — are the configured list
 * moneta.currencies.display, so adding one is a config change, not a migration.
 */
@Service
public class CurrencyService {

    private final List<String> displayCurrencies;
    private final FxRateRepository fxRateRepository;

    public CurrencyService(@Value("${moneta.currencies.display}") List<String> displayCurrencies,
                           FxRateRepository fxRateRepository) {
        this.displayCurrencies = List.copyOf(displayCurrencies);
        this.fxRateRepository = fxRateRepository;
    }

    /** The configured display currencies, in config order. */
    public List<String> displayCurrencies() {
        return displayCurrencies;
    }

    /** May a user pick this as their preferred currency? */
    public boolean isDisplayCurrency(String code) {
        return displayCurrencies.contains(code);
    }

    /**
     * May a row be stored in this currency? Any code the latest FX set covers, plus
     * the display currencies, so AUD and USD work before the first fetch.
     */
    public boolean isSupportedCurrency(String code) {
        if (displayCurrencies.contains(code)) {
            return true;
        }
        return fxRateRepository.findLatestRateDate()
                .map(latest -> fxRateRepository.findCurrencyCodesOn(latest).contains(code))
                .orElse(false);
    }

    /** The display list, plus how fresh the rates are; ratesAsOf is null before the first fetch. */
    public CurrenciesResponse buildCurrenciesResponse() {
        CurrenciesResponse response = new CurrenciesResponse();
        response.setDisplay(displayCurrencies);
        response.setRatesAsOf(fxRateRepository.findLatestRateDate().orElse(null));
        return response;
    }
}
