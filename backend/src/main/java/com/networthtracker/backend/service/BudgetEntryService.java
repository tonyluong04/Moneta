package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.BudgetEntryResponse;
import com.networthtracker.backend.dto.CategorySummary;
import com.networthtracker.backend.dto.CreateBudgetEntryRequest;
import com.networthtracker.backend.dto.UpdateBudgetEntryRequest;
import com.networthtracker.backend.entity.BudgetEntry;
import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.BudgetEntryRepository;
import com.networthtracker.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetEntryService {

    private final BudgetEntryRepository budgetEntryRepository;
    private final CategoryRepository categoryRepository;

    public BudgetEntryService(BudgetEntryRepository budgetEntryRepository,
                              CategoryRepository categoryRepository) {
        this.budgetEntryRepository = budgetEntryRepository;
        this.categoryRepository = categoryRepository;
    }

    // "show me July's budget"
    public List<BudgetEntryResponse> findByMonth(User user, LocalDate month) {
        // squash whatever date came in down to the 1st (so 2026-07-15 → 2026-07-01).
        LocalDate firstOfMonth = toFirstOfMonth(month);
        // get database rows using BudgetEntryRepository
        List<BudgetEntry> entries = budgetEntryRepository.findByUserAndMonth(user, firstOfMonth);
        // Loop over them, converting each BudgetEntry (internal entity)
        //  into a BudgetEntryResponse (safe DTO)
        //  using the toResponse helper
        List<BudgetEntryResponse> responses = new ArrayList<>();
        for (BudgetEntry entry : entries) {
            responses.add(toResponse(entry));
        }
        return responses;
    }

    // create (insert) one new budget row
    @Transactional
    public BudgetEntryResponse create(User user, CreateBudgetEntryRequest request) {
        // squash whatever date came in down to the 1st (so 2026-07-15 → 2026-07-01).
        LocalDate month = toFirstOfMonth(request.getMonth());
        //  turn the category ID number into the real Category object,
        //  and verify you're allowed to use it
        Category category = resolveCategory(request.getCategoryId());
        if (budgetEntryRepository.existsByUserAndCategoryAndMonth(user, category, month)) {
            throw new IllegalArgumentException(
                    "A budget for that category already exists for this month");
        }
        // Build a fresh BudgetEntry,
        //  set its fields from the request: use, category, month, budgeted amount.
        BudgetEntry entry = new BudgetEntry();
        entry.setUser(user);
        entry.setCategory(category);
        entry.setMonth(month);
        entry.setBudgetedAmount(request.getBudgetedAmount());
        if (request.getActualAmount() != null) {
            entry.setActualAmount(request.getActualAmount());
        }
        if (request.getCurrency() != null) {
            entry.setCurrency(request.getCurrency());
        }
        entry.setNotes(request.getNotes());

        entry = budgetEntryRepository.save(entry); // write to database
        return toResponse(entry); //convert to DTO response and return
    }


    // change an existing row's amounts/notes
    @Transactional
    public BudgetEntryResponse update(User user, Integer id, UpdateBudgetEntryRequest request) {
        // fetch the row but only if it's yours.
        BudgetEntry entry = budgetEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Budget entry not found"));

        if (request.getBudgetedAmount() != null) {
            entry.setBudgetedAmount(request.getBudgetedAmount());
        }
        if (request.getActualAmount() != null) {
            entry.setActualAmount(request.getActualAmount());
        }
        if (request.getCurrency() != null) {
            entry.setCurrency(request.getCurrency());
        }
        if (request.getNotes() != null) {
            entry.setNotes(request.getNotes());
        }

        entry = budgetEntryRepository.save(entry); // write to DB
        return toResponse(entry); // convert to DTO response and return
    }

    // delete one budget row
    @Transactional
    public void delete(User user, Integer id) {
        BudgetEntry entry = budgetEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Budget entry not found"));
        budgetEntryRepository.delete(entry);
    }

    // the "copy last month" button, so a user doesn't re-enter every category each month.
    @Transactional
    public List<BudgetEntryResponse> copyForward(User user, LocalDate targetMonth) {
        LocalDate target = toFirstOfMonth(targetMonth); // squash to 1st July
        LocalDate source = target.minusMonths(1); // June

        // load all June rows
        List<BudgetEntry> sourceEntries = budgetEntryRepository.findByUserAndMonth(user, source);

        // loop through every rows
        List<BudgetEntryResponse> created = new ArrayList<>();
        for (BudgetEntry sourceEntry : sourceEntries) {
            // skip if July already has that category
            boolean alreadyExists = budgetEntryRepository
                    .existsByUserAndCategoryAndMonth(user, sourceEntry.getCategory(), target);
            if (alreadyExists) {
                continue;
            }
            // otherwise, build a copy
            BudgetEntry copy = new BudgetEntry();
            copy.setUser(user);
            copy.setCategory(sourceEntry.getCategory());
            copy.setMonth(target);
            copy.setBudgetedAmount(sourceEntry.getBudgetedAmount());
            copy.setActualAmount(BigDecimal.ZERO); // start fresh — actuals are not carried over
            copy.setCurrency(sourceEntry.getCurrency());
            copy.setNotes(sourceEntry.getNotes());

            copy = budgetEntryRepository.save(copy);
            created.add(toResponse(copy));
        }
        return created;
    }

    // convert an incoming categoryId into a Category object.
    //  categories are a shared, seeded set, so any real id is usable by anyone.
    private Category resolveCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    // squash any date to 1st of the month
    private LocalDate toFirstOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    // convert a DB row into a BudgetEntryResponse DTO
    private BudgetEntryResponse toResponse(BudgetEntry entry) {
        Category category = entry.getCategory();

        CategorySummary categorySummary = new CategorySummary();
        categorySummary.setId(category.getId());
        categorySummary.setName(category.getName());
        categorySummary.setColour(category.getColour());

        BudgetEntryResponse response = new BudgetEntryResponse();
        response.setId(entry.getId());
        response.setCategory(categorySummary);
        response.setMonth(entry.getMonth());
        response.setBudgetedAmount(entry.getBudgetedAmount());
        response.setActualAmount(entry.getActualAmount());
        response.setCurrency(entry.getCurrency());
        response.setNotes(entry.getNotes());
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());
        return response;
    }
}
