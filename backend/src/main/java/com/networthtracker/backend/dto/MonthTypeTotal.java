package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;

// one row of the "sum actuals by month and type" aggregate.
// spring data builds a proxy implementing this, matching the query's column
// aliases to the getter names — so the "AS month"/"AS type"/"AS total" in the
// @Query must stay in step with the three getters below.
public interface MonthTypeTotal {
    LocalDate getMonth();
    CategoryType getType();
    BigDecimal getTotal();
}
