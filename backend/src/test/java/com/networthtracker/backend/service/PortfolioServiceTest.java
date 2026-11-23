package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.AssetCategoryTotal;
import com.networthtracker.backend.dto.PortfolioSummaryResponse;
import com.networthtracker.backend.entity.Asset;
import com.networthtracker.backend.entity.AssetCategory;
import com.networthtracker.backend.entity.FxRate;
import com.networthtracker.backend.entity.FxRateId;
import com.networthtracker.backend.entity.Liability;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.AssetCategoryRepository;
import com.networthtracker.backend.repository.AssetRepository;
import com.networthtracker.backend.repository.FxRateRepository;
import com.networthtracker.backend.repository.LiabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * The seam is buildSummary, with the real CurrencyConverter over a mocked rate
 * store: convert each row, then sum, then round once to the preferred currency.
 */
@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock private AssetRepository assetRepository;
    @Mock private LiabilityRepository liabilityRepository;
    @Mock private AssetCategoryRepository assetCategoryRepository;
    @Mock private FxRateRepository fxRateRepository;

    private PortfolioService service;
    private User user;
    private AssetCategory cash;
    private AssetCategory shares;
    private final List<Asset> assets = new ArrayList<>();
    private final List<Liability> liabilities = new ArrayList<>();

    private static final LocalDate RATE_DAY = LocalDate.of(2026, 11, 20);

    @BeforeEach
    void setUp() {
        service = new PortfolioService(assetRepository, liabilityRepository,
                assetCategoryRepository, new CurrencyConverter(fxRateRepository));
        user = new User();
        user.setPreferredCurrency("AUD");
        cash = category(1, "Cash");
        shares = category(2, "Shares");

        when(assetRepository.findByUserOrdered(user)).thenReturn(assets);
        when(liabilityRepository.findByUserOrdered(user)).thenReturn(liabilities);
        when(assetCategoryRepository.findAllOrdered()).thenReturn(List.of(cash, shares));
        lenient().when(fxRateRepository.findLatestRateDate()).thenReturn(Optional.of(RATE_DAY));
        rate("USD", "1");
    }

    @Test
    void mixedCurrencyRowsAreConvertedBeforeTheyAreSummed() {
        rate("AUD", "1.5");
        asset(cash, "1000.00", "AUD");
        asset(cash, "100.00", "USD");   // 150 AUD
        liability("200.00", "USD");     // 300 AUD

        PortfolioSummaryResponse summary = service.buildSummary(user);

        assertThat(summary.getCurrency()).isEqualTo("AUD");
        assertThat(summary.getTotalAssets()).isEqualByComparingTo("1150.00");
        assertThat(summary.getTotalLiabilities()).isEqualByComparingTo("300.00");
        assertThat(summary.getNetWorth()).isEqualByComparingTo("850.00");
        assertThat(categoryTotal(summary, "Cash")).isEqualByComparingTo("1150.00");
        assertThat(categoryTotal(summary, "Shares")).isEqualByComparingTo("0");
    }

    @Test
    void totalsAreRoundedOnceNotSummedFromRoundedRows() {
        // each US$0.01 is A$0.01333…, which would round to A$0.01 on its own;
        // three of them, summed unrounded, are A$0.04 — not A$0.03
        rate("AUD", "1.333333");
        asset(cash, "0.01", "USD");
        asset(cash, "0.01", "USD");
        asset(shares, "0.01", "USD");

        PortfolioSummaryResponse summary = service.buildSummary(user);

        assertThat(summary.getTotalAssets()).isEqualByComparingTo("0.04");
        assertThat(summary.getNetWorth()).isEqualByComparingTo("0.04");
        assertThat(categoryTotal(summary, "Cash")).isEqualByComparingTo("0.03");
    }

    @Test
    void totalsAreRoundedToThePreferredCurrencysMinorUnit() {
        user.setPreferredCurrency("JPY");
        rate("JPY", "150.123");
        asset(cash, "10.00", "USD");    // ¥1,501.23

        PortfolioSummaryResponse summary = service.buildSummary(user);

        assertThat(summary.getTotalAssets()).isEqualTo(new BigDecimal("1501"));
    }

    private void rate(String code, String unitsPerUsd) {
        lenient().when(fxRateRepository.findById(new FxRateId(code, RATE_DAY)))
                .thenReturn(Optional.of(new FxRate(new FxRateId(code, RATE_DAY), new BigDecimal(unitsPerUsd))));
    }

    private static AssetCategory category(int id, String name) {
        AssetCategory category = new AssetCategory();
        category.setId(id);
        category.setName(name);
        return category;
    }

    private void asset(AssetCategory category, String value, String currency) {
        Asset asset = new Asset();
        asset.setCategory(category);
        asset.setManualValue(new BigDecimal(value));
        asset.setCurrency(currency);
        assets.add(asset);
    }

    private void liability(String balance, String currency) {
        Liability liability = new Liability();
        liability.setBalance(new BigDecimal(balance));
        liability.setCurrency(currency);
        liabilities.add(liability);
    }

    private static BigDecimal categoryTotal(PortfolioSummaryResponse summary, String name) {
        return summary.getAssetCategories().stream()
                .filter(total -> total.getName().equals(name))
                .map(AssetCategoryTotal::getTotal)
                .findFirst()
                .orElseThrow();
    }
}
