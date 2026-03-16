package com.casting.platform.repository;

import com.casting.platform.entity.CustomerSubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerSubscriptionPlanRepository extends JpaRepository<CustomerSubscriptionPlan, Long> {
    List<CustomerSubscriptionPlan> findByActiveTrueOrderByIdAsc();
}
