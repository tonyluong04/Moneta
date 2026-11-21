package com.networthtracker.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * The parts of ExchangeRate-API's open endpoint (GET /v6/latest/USD) that Moneta
 * reads. Everything else in the body (provider, terms, next update) is ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateFeedResponse {

    /** "success" or "error". */
    private String result;

    /** When the feed last updated, as epoch seconds — the same instant as time_last_update_utc. */
    @JsonProperty("time_last_update_unix")
    private Long timeLastUpdateUnix;

    /** Currency code → units per 1 USD, USD itself included at 1. */
    private Map<String, BigDecimal> rates;

    public ExchangeRateFeedResponse() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getTimeLastUpdateUnix() {
        return timeLastUpdateUnix;
    }

    public void setTimeLastUpdateUnix(Long timeLastUpdateUnix) {
        this.timeLastUpdateUnix = timeLastUpdateUnix;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
