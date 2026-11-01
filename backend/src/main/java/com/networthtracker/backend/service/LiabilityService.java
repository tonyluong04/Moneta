package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.CreateLiabilityRequest;
import com.networthtracker.backend.dto.LiabilityCategorySummary;
import com.networthtracker.backend.dto.LiabilityResponse;
import com.networthtracker.backend.dto.UpdateLiabilityRequest;
import com.networthtracker.backend.entity.Liability;
import com.networthtracker.backend.entity.LiabilityCategory;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.LiabilityCategoryRepository;
import com.networthtracker.backend.repository.LiabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiabilityService {

    private final LiabilityRepository liabilityRepository;
    private final LiabilityCategoryRepository liabilityCategoryRepository;

    public LiabilityService(LiabilityRepository liabilityRepository,
                            LiabilityCategoryRepository liabilityCategoryRepository) {
        this.liabilityRepository = liabilityRepository;
        this.liabilityCategoryRepository = liabilityCategoryRepository;
    }

    /** Everything the user owes, mortgages first — ordered by the query. */
    public List<LiabilityResponse> findAll(User user) {
        List<LiabilityResponse> responses = new ArrayList<>();
        for (Liability liability : liabilityRepository.findByUserOrdered(user)) {
            responses.add(toResponse(liability));
        }
        return responses;
    }

    public LiabilityResponse findOne(User user, Integer id) {
        return toResponse(requireOwn(user, id));
    }

    @Transactional
    public LiabilityResponse create(User user, CreateLiabilityRequest request) {
        Liability liability = new Liability();
        liability.setUser(user);
        liability.setName(request.getName().trim());
        liability.setCategory(resolveCategory(request.getCategoryId()));
        liability.setBalance(request.getBalance());
        liability.setInterestRate(request.getInterestRate());
        if (request.getCurrency() != null) {
            liability.setCurrency(request.getCurrency());
        }
        liability.setNotes(request.getNotes());

        liability = liabilityRepository.save(liability);
        return toResponse(liability);
    }

    /** Partial update — paying down a loan is a PUT carrying only the new balance. */
    @Transactional
    public LiabilityResponse update(User user, Integer id, UpdateLiabilityRequest request) {
        Liability liability = requireOwn(user, id);

        if (request.getName() != null) {
            liability.setName(request.getName().trim());
        }
        if (request.getCategoryId() != null) {
            liability.setCategory(resolveCategory(request.getCategoryId()));
        }
        if (request.getBalance() != null) {
            liability.setBalance(request.getBalance());
        }
        if (request.getInterestRate() != null) {
            liability.setInterestRate(request.getInterestRate());
        }
        if (request.getCurrency() != null) {
            liability.setCurrency(request.getCurrency());
        }
        if (request.getNotes() != null) {
            liability.setNotes(request.getNotes());
        }

        liability = liabilityRepository.save(liability);
        return toResponse(liability);
    }

    @Transactional
    public void delete(User user, Integer id) {
        liabilityRepository.delete(requireOwn(user, id));
    }

    // seeded, shared set — see AssetService.resolveCategory
    private LiabilityCategory resolveCategory(Integer categoryId) {
        return liabilityCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    private Liability requireOwn(User user, Integer id) {
        return liabilityRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Liability not found"));
    }

    private LiabilityResponse toResponse(Liability liability) {
        LiabilityCategory category = liability.getCategory();

        LiabilityCategorySummary categorySummary = new LiabilityCategorySummary();
        categorySummary.setId(category.getId());
        categorySummary.setName(category.getName());

        LiabilityResponse response = new LiabilityResponse();
        response.setId(liability.getId());
        response.setName(liability.getName());
        response.setCategory(categorySummary);
        response.setCurrency(liability.getCurrency());
        response.setBalance(liability.getBalance());
        response.setInterestRate(liability.getInterestRate());
        response.setNotes(liability.getNotes());
        response.setCreatedAt(liability.getCreatedAt());
        response.setUpdatedAt(liability.getUpdatedAt());
        return response;
    }
}
