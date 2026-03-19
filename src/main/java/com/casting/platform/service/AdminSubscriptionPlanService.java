package com.casting.platform.service;

import com.casting.platform.dto.request.admin.AdminPlanBasicsRequest;
import com.casting.platform.dto.request.admin.AdminPlanBoosterRequest;
import com.casting.platform.dto.request.admin.AdminPlanCastingRequest;
import com.casting.platform.dto.request.admin.AdminPlanPremiumRequest;
import com.casting.platform.dto.request.admin.AdminSubscriptionPlanRequest;
import com.casting.platform.dto.response.admin.AdminSubscriptionPlanResponse;
import com.casting.platform.entity.CustomerSubscriptionPlan;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminSubscriptionPlanService {

    private final CustomerSubscriptionPlanRepository planRepository;

    public List<AdminSubscriptionPlanResponse> getAll() {
        return planRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AdminSubscriptionPlanResponse create(AdminSubscriptionPlanRequest req) {
        CustomerSubscriptionPlan plan = new CustomerSubscriptionPlan();
        apply(plan, req);
        planRepository.save(plan);
        return toResponse(plan);
    }

    public AdminSubscriptionPlanResponse createBasics(AdminPlanBasicsRequest req) {
        CustomerSubscriptionPlan plan = new CustomerSubscriptionPlan();
        applyBasics(plan, req);
        planRepository.save(plan);
        return toResponse(plan);
    }

    public AdminSubscriptionPlanResponse update(Long id, AdminSubscriptionPlanRequest req) {
        CustomerSubscriptionPlan plan = getPlanOrThrow(id);
        apply(plan, req);
        planRepository.save(plan);
        return toResponse(plan);
    }

    public AdminSubscriptionPlanResponse updateBasics(Long id, AdminPlanBasicsRequest req) {
        CustomerSubscriptionPlan plan = getPlanOrThrow(id);
        applyBasics(plan, req);
        planRepository.save(plan);
        return toResponse(plan);
    }

    public AdminSubscriptionPlanResponse updateBooster(Long id, AdminPlanBoosterRequest req) {
        CustomerSubscriptionPlan plan = getPlanOrThrow(id);
        validateMoney(req.getBoosterPrice(), "Booster price");
        plan.setBoosterPrice(req.getBoosterPrice());
        plan.setBoosterContacts(req.getBoosterContacts());
        planRepository.save(plan);
        return toResponse(plan);
    }

    public AdminSubscriptionPlanResponse updateCasting(Long id, AdminPlanCastingRequest req) {
        CustomerSubscriptionPlan plan = getPlanOrThrow(id);
        validateMoney(req.getCastingPostPrice(), "Casting post price");
        plan.setCastingPostPrice(req.getCastingPostPrice());
        plan.setCastingPostDays(req.getCastingPostDays());
        planRepository.save(plan);
        return toResponse(plan);
    }

    public AdminSubscriptionPlanResponse updatePremium(Long id, AdminPlanPremiumRequest req) {
        CustomerSubscriptionPlan plan = getPlanOrThrow(id);
        validateMoney(req.getPremiumProfilePrice(), "Premium profile price");
        if (req.getPremiumProfileDays() <= 0) {
            throw new BadRequestException("Premium profile days must be greater than 0");
        }
        plan.setPremiumProfilePrice(req.getPremiumProfilePrice());
        plan.setPremiumProfileDays(req.getPremiumProfileDays());
        planRepository.save(plan);
        return toResponse(plan);
    }

    public void delete(Long id) {
        planRepository.deleteById(id);
    }

    private void apply(CustomerSubscriptionPlan plan, AdminSubscriptionPlanRequest req) {
        validateRequest(req);
        applyBasics(plan, toBasicsRequest(req));
        plan.setBoosterPrice(req.getBoosterPrice());
        plan.setBoosterContacts(req.getBoosterContacts());
        plan.setCastingPostPrice(req.getCastingPostPrice());
        plan.setCastingPostDays(req.getCastingPostDays());
        plan.setPremiumProfilePrice(req.getPremiumProfilePrice());
        plan.setPremiumProfileDays(req.getPremiumProfileDays());
    }

    private void applyBasics(CustomerSubscriptionPlan plan, AdminPlanBasicsRequest req) {
        validateMoney(req.getPricePerPeriod(), "Subscription price");
        plan.setName(req.getName());
        plan.setPricePerPeriod(req.getPricePerPeriod());
        plan.setPeriodDays(req.getPeriodDays());
        plan.setBaseContactLimit(req.getBaseContactLimit());
        plan.setActive(req.isActive());
        if (req.isActive()) {
            deactivateOtherPlans(plan.getId());
        }
    }

    private void validateRequest(AdminSubscriptionPlanRequest req) {
        validateMoney(req.getBoosterPrice(), "Booster price");
        validateMoney(req.getCastingPostPrice(), "Casting post price");
        validateMoney(req.getPremiumProfilePrice(), "Premium profile price");
        if (req.getPremiumProfileDays() <= 0) {
            throw new BadRequestException("Premium profile days must be greater than 0");
        }
    }

    private CustomerSubscriptionPlan getPlanOrThrow(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Plan not found"));
    }

    private AdminPlanBasicsRequest toBasicsRequest(AdminSubscriptionPlanRequest req) {
        AdminPlanBasicsRequest basics = new AdminPlanBasicsRequest();
        basics.setName(req.getName());
        basics.setPricePerPeriod(req.getPricePerPeriod());
        basics.setPeriodDays(req.getPeriodDays());
        basics.setBaseContactLimit(req.getBaseContactLimit());
        basics.setActive(req.isActive());
        return basics;
    }

    private void validateMoney(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " is required");
        }
        if (value.signum() < 0) {
            throw new BadRequestException(fieldName + " cannot be negative");
        }
    }

    private void deactivateOtherPlans(Long currentPlanId) {
        List<CustomerSubscriptionPlan> activePlans = planRepository.findByActiveTrueOrderByIdAsc();
        for (CustomerSubscriptionPlan activePlan : activePlans) {
            if (currentPlanId != null && currentPlanId.equals(activePlan.getId())) {
                continue;
            }
            activePlan.setActive(false);
        }
    }

    private AdminSubscriptionPlanResponse toResponse(CustomerSubscriptionPlan plan) {
        AdminSubscriptionPlanResponse dto = new AdminSubscriptionPlanResponse();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setPricePerPeriod(plan.getPricePerPeriod());
        dto.setPeriodDays(plan.getPeriodDays());
        dto.setBaseContactLimit(plan.getBaseContactLimit());
        dto.setBoosterPrice(plan.getBoosterPrice());
        dto.setBoosterContacts(plan.getBoosterContacts());
        dto.setCastingPostPrice(plan.getCastingPostPrice());
        dto.setCastingPostDays(plan.getCastingPostDays());
        dto.setPremiumProfilePrice(plan.getPremiumProfilePrice());
        dto.setPremiumProfileDays(plan.getPremiumProfileDays());
        dto.setActive(plan.isActive());
        return dto;
    }
}
