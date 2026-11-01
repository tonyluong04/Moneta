package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.CategoryOption;
import com.networthtracker.backend.dto.CreateLiabilityRequest;
import com.networthtracker.backend.dto.LiabilityResponse;
import com.networthtracker.backend.dto.UpdateLiabilityRequest;
import com.networthtracker.backend.entity.Liability;
import com.networthtracker.backend.entity.LiabilityCategory;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.LiabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LiabilityService {

    private final LiabilityRepository liabilityRepository;

    public LiabilityService(LiabilityRepository liabilityRepository) {
        this.liabilityRepository = liabilityRepository;
    }

    /** The seven categories, for the add form. Description comes back null. */
    public List<CategoryOption> findCategories() {
        List<CategoryOption> options = new ArrayList<>();
        for (LiabilityCategory category : LiabilityCategory.values()) {
            CategoryOption option = new CategoryOption();
            option.setValue(category.name());
            option.setLabel(category.getLabel());
            option.setDisplayOrder(category.getDisplayOrder());
            options.add(option);
        }
        return options;
    }

    /** Every liability the user owes, mortgages first. */
    public List<LiabilityResponse> findAll(User user) {
        List<Liability> liabilities = liabilityRepository.findByUserOrderByCategoryAscNameAsc(user);

        // same reason as AssetService.findAll: the column sorts alphabetically,
        // displayOrder lives on the enum, and the stable sort keeps names in order
        liabilities.sort(Comparator.comparingInt(
                (Liability liability) -> liability.getCategory().getDisplayOrder()));

        List<LiabilityResponse> responses = new ArrayList<>();
        for (Liability liability : liabilities) {
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
        liability.setCategory(request.getCategory());
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
        if (request.getCategory() != null) {
            liability.setCategory(request.getCategory());
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

    private Liability requireOwn(User user, Integer id) {
        return liabilityRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Liability not found"));
    }

    private LiabilityResponse toResponse(Liability liability) {
        LiabilityResponse response = new LiabilityResponse();
        response.setId(liability.getId());
        response.setName(liability.getName());
        response.setCategory(liability.getCategory());
        response.setCurrency(liability.getCurrency());
        response.setBalance(liability.getBalance());
        response.setInterestRate(liability.getInterestRate());
        response.setNotes(liability.getNotes());
        response.setCreatedAt(liability.getCreatedAt());
        response.setUpdatedAt(liability.getUpdatedAt());
        return response;
    }
}
