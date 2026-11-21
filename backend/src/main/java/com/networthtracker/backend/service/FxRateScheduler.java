package com.networthtracker.backend.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * When rates are fetched: once a day, and once at startup if today's set is
 * missing (PRD §4.5.2). Off when moneta.fx.enabled=false, so tests and offline
 * runs never reach the network; the stored rates still work without it.
 */
@Component
@ConditionalOnProperty(name = "moneta.fx.enabled", havingValue = "true", matchIfMissing = true)
public class FxRateScheduler {

    private final FxRateService fxRateService;

    public FxRateScheduler(FxRateService fxRateService) {
        this.fxRateService = fxRateService;
    }

    // the feed updates just after 00:00 UTC; an hour later it is reliably fresh
    @Scheduled(cron = "0 0 1 * * *", zone = "UTC")
    public void fetchDaily() {
        fxRateService.fetchAndStore();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetchOnStartupIfMissing() {
        if (fxRateService.isTodayMissing()) {
            fxRateService.fetchAndStore();
        }
    }
}
