package com.casting.platform.service;

import com.casting.platform.dto.response.customer.CustomerPlanResponse;
import com.casting.platform.entity.CustomerSubscriptionPlan;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerPlanService {

    private final CustomerSubscriptionPlanRepository planRepository;

    public List<CustomerPlanResponse> getActivePlans() {
        return planRepository.findAll().stream()
                .filter(CustomerSubscriptionPlan::isActive)
                .map(this::toDto)
                .toList();
    }

    private CustomerPlanResponse toDto(CustomerSubscriptionPlan plan) {
        CustomerPlanResponse dto = new CustomerPlanResponse();
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
