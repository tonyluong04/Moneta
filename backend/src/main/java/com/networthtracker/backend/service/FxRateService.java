package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.ExchangeRateFeedResponse;
import com.networthtracker.backend.entity.FxRate;
import com.networthtracker.backend.entity.FxRateId;
import com.networthtracker.backend.repository.FxRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fetches the day's exchange rates and appends them to fx_rates (PRD §4.5.2).
 *
 * A failed fetch never throws: it is logged and the stored rates stay in use, so
 * a feed outage shows up as "rates as of" an older date rather than as errors.
 */
@Service
public class FxRateService {

    private static final Logger log = LoggerFactory.getLogger(FxRateService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final FxRateRepository fxRateRepository;
    private final RestClient feedClient;

    public FxRateService(FxRateRepository fxRateRepository,
                         @Value("${moneta.fx.url}") String feedUrl) {
        this.fxRateRepository = fxRateRepository;

        // a hung feed must not hold up startup or the scheduler thread
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT);
        requestFactory.setReadTimeout(TIMEOUT);
        this.feedClient = RestClient.builder()
                .baseUrl(feedUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /** True when the newest stored set is older than today (UTC), or nothing is stored. */
    public boolean isTodayMissing() {
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        return fxRateRepository.findLatestRateDate()
                .map(latest -> latest.isBefore(todayUtc))
                .orElse(true);
    }

    /**
     * Fetch the feed and store its rates under the feed's own update date. If that
     * date is already stored (the feed has not updated since), nothing is written:
     * history is append only.
     */
    public void fetchAndStore() {
        ExchangeRateFeedResponse feed;
        try {
            feed = feedClient.get().retrieve().body(ExchangeRateFeedResponse.class);
        } catch (RuntimeException e) {
            log.warn("FX fetch failed, keeping the stored rates: {}", e.getMessage());
            return;
        }

        if (feed == null || !"success".equals(feed.getResult())
                || feed.getTimeLastUpdateUnix() == null || feed.getRates() == null) {
            log.warn("FX feed did not return a usable result, keeping the stored rates");
            return;
        }

        LocalDate rateDate = Instant.ofEpochSecond(feed.getTimeLastUpdateUnix())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        if (fxRateRepository.existsByIdRateDate(rateDate)) {
            log.info("FX rates for {} are already stored, nothing to add", rateDate);
            return;
        }

        List<FxRate> rates = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : feed.getRates().entrySet()) {
            String code = entry.getKey();
            BigDecimal rate = entry.getValue();
            // the table's CHECK (rate > 0) and VARCHAR(3) would reject the whole set over one bad entry
            if (code == null || !code.matches("[A-Z]{3}") || rate == null || rate.signum() <= 0) {
                log.warn("Skipping FX entry {}={}", code, rate);
                continue;
            }
            rates.add(new FxRate(new FxRateId(code, rateDate), rate));
        }

        try {
            fxRateRepository.saveAll(rates);
            log.info("Stored {} FX rates for {}", rates.size(), rateDate);
        } catch (RuntimeException e) {
            log.warn("Could not store FX rates for {}: {}", rateDate, e.getMessage());
        }
    }
}
