package com.networthtracker.backend.service;

import com.networthtracker.backend.entity.FxRate;
import com.networthtracker.backend.entity.FxRateId;
import com.networthtracker.backend.repository.FxRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterTest {

    @Mock
    private FxRateRepository fxRateRepository;

    @InjectMocks
    private CurrencyConverter converter;

    private static final LocalDate NOV_1 = LocalDate.of(2026, 11, 1);
    private static final LocalDate NOV_5 = LocalDate.of(2026, 11, 5);

    /** Stores one rate (units per 1 USD) in a day's set. */
    private void rate(String code, LocalDate date, String unitsPerUsd) {
        lenient().when(fxRateRepository.findById(new FxRateId(code, date)))
                .thenReturn(Optional.of(new FxRate(new FxRateId(code, date), new BigDecimal(unitsPerUsd))));
    }

    @Test
    void sameCurrencyIsReturnedUnchangedWithoutLookingUpARate() {
        BigDecimal result = converter.convert(new BigDecimal("1250.40"), "AUD", "AUD");

        assertThat(result).isEqualByComparingTo("1250.40");
        verifyNoInteractions(fxRateRepository);
    }

    @Test
    void usdToAudMultipliesByTheLatestAudRate() {
        when(fxRateRepository.findLatestRateDate()).thenReturn(Optional.of(NOV_1));
        rate("USD", NOV_1, "1");
        rate("AUD", NOV_1, "1.5");

        BigDecimal result = converter.convert(new BigDecimal("100.00"), "USD", "AUD");

        assertThat(result).isEqualByComparingTo("150");
    }

    @Test
    void crossRateGoesThroughUsd() {
        // 300 AUD = 200 USD at 1.5, and 200 USD = 5,000,000 VND at 25,000
        when(fxRateRepository.findLatestRateDate()).thenReturn(Optional.of(NOV_1));
        rate("AUD", NOV_1, "1.5");
        rate("VND", NOV_1, "25000");

        BigDecimal result = converter.convert(new BigDecimal("300"), "AUD", "VND");

        assertThat(result).isEqualByComparingTo("5000000");
    }

    @Test
    void resultIsNotRoundedToTwoPlaces() {
        // 1 AUD at 1.5 per USD is two thirds of a dollar; callers round once, later
        when(fxRateRepository.findLatestRateDate()).thenReturn(Optional.of(NOV_1));
        rate("AUD", NOV_1, "1.5");
        rate("USD", NOV_1, "1");

        BigDecimal result = converter.convert(BigDecimal.ONE, "AUD", "USD");

        assertThat(result).isEqualByComparingTo("0.666666666667");
    }

    @Test
    void datedConversionUsesTheNewestSetOnOrBeforeThatDate() {
        // no set on Nov 5 itself; Nov 1's AUD rate of 1.4 applies
        when(fxRateRepository.findLatestRateDateOnOrBefore(NOV_5)).thenReturn(Optional.of(NOV_1));
        rate("USD", NOV_1, "1");
        rate("AUD", NOV_1, "1.4");

        BigDecimal result = converter.convert(new BigDecimal("100"), "USD", "AUD", NOV_5);

        assertThat(result).isEqualByComparingTo("140");
    }

    @Test
    void dateBeforeAllHistoryFallsBackToTheOldestSet() {
        LocalDate longAgo = LocalDate.of(2020, 1, 1);
        when(fxRateRepository.findLatestRateDateOnOrBefore(longAgo)).thenReturn(Optional.empty());
        when(fxRateRepository.findOldestRateDate()).thenReturn(Optional.of(NOV_1));
        rate("USD", NOV_1, "1");
        rate("AUD", NOV_1, "1.4");

        BigDecimal result = converter.convert(new BigDecimal("100"), "USD", "AUD", longAgo);

        assertThat(result).isEqualByComparingTo("140");
    }

    @Test
    void missingCurrencyThrowsNamingTheCode() {
        when(fxRateRepository.findLatestRateDate()).thenReturn(Optional.of(NOV_1));
        rate("USD", NOV_1, "1");
        when(fxRateRepository.findById(new FxRateId("XYZ", NOV_1))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> converter.convert(BigDecimal.TEN, "USD", "XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No exchange rate for XYZ");
    }

    @Test
    void noRatesStoredAtAllThrows() {
        when(fxRateRepository.findLatestRateDate()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> converter.convert(BigDecimal.TEN, "USD", "AUD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No exchange rates stored yet");
    }
}
