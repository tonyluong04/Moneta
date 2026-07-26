package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.BudgetEntry;
import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
