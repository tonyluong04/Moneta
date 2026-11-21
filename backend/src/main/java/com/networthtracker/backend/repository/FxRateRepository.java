package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.FxRate;
import com.networthtracker.backend.entity.FxRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FxRateRepository extends JpaRepository<FxRate, FxRateId> {

    /** The date of the newest stored set; empty before the first fetch. */
    @Query("SELECT MAX(r.id.rateDate) FROM FxRate r")
    Optional<LocalDate> findLatestRateDate();

    /** The newest set on or before {@code date} — what a dated conversion uses. */
    @Query("SELECT MAX(r.id.rateDate) FROM FxRate r WHERE r.id.rateDate <= :date")
    Optional<LocalDate> findLatestRateDateOnOrBefore(@Param("date") LocalDate date);

    /** The first day of history; a date before it falls back to this set. */
    @Query("SELECT MIN(r.id.rateDate) FROM FxRate r")
    Optional<LocalDate> findOldestRateDate();

    /** Append-only guard: a set already stored for this date is never overwritten. */
    boolean existsByIdRateDate(LocalDate rateDate);

    /** Every currency code in one day's set. */
    @Query("SELECT r.id.quoteCurrency FROM FxRate r WHERE r.id.rateDate = :date")
    List<String> findCurrencyCodesOn(@Param("date") LocalDate date);
}
