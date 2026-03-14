package com.casting.platform.repository;

import com.casting.platform.entity.CustomerSubscription;
import com.casting.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomerSubscriptionRepository extends JpaRepository<CustomerSubscription, Long> {

    @Query("""
           SELECT s FROM CustomerSubscription s
           WHERE s.customer = :customer
             AND s.active = true
             AND s.expiresAt > :now
           ORDER BY s.startedAt DESC
           """)
    Optional<CustomerSubscription> findActiveSubscription(User customer, LocalDateTime now);

    Optional<CustomerSubscription> findByPaymentId(String paymentId);
}
