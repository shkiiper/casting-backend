package com.casting.platform.repository;

import com.casting.platform.entity.CustomerSubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSubscriptionPlanRepository extends JpaRepository<CustomerSubscriptionPlan, Long> {
}
