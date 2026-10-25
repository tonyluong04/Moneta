package com.networthtracker.backend.repository;

import com.networthtracker.backend.dto.MonthTypeTotal;
import com.networthtracker.backend.entity.BudgetEntry;
import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// database access for budget entries.
public interface BudgetEntryRepository extends JpaRepository<BudgetEntry, Integer> {

    // all of a user's rows for one month (powers the setup screen, actuals form, dashboard)
    List<BudgetEntry> findByUserAndMonth(User user, LocalDate month);

    // find a row only if it's this user's (used before update/delete for safety)
    Optional<BudgetEntry> findByIdAndUser(Integer id, User user);

    // true if this user already has a row for this category+month (blocks duplicates)
    boolean existsByUserAndCategoryAndMonth(User user, Category category, LocalDate month);

    /**
     * Actual totals per month per category type, across every month this user has data for.
     * Powers the money saved/lost line — at most two rows per month, so it stays small
     * however long the user has been budgeting.
     *
     * The AS aliases must match MonthTypeTotal getter names.
     */
    @Query("SELECT b.month AS month, c.type AS type, SUM(b.actualAmount) AS total "
            + "FROM BudgetEntry b JOIN b.category c "
            + "WHERE b.user = :user "
            + "GROUP BY b.month, c.type "
            + "ORDER BY b.month ASC")
    List<MonthTypeTotal> sumActualByMonthAndType(@Param("user") User user);
}
