package com.casting.platform.repository;

import com.casting.platform.entity.CustomerSubscription;
import com.casting.platform.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerSubscriptionRepository extends JpaRepository<CustomerSubscription, Long> {

    @Query("""
           SELECT s FROM CustomerSubscription s
           WHERE s.customer = :customer
             AND s.active = true
             AND s.expiresAt > :now
           ORDER BY s.startedAt DESC NULLS LAST, s.id DESC
           """)
    List<CustomerSubscription> findActiveSubscriptions(
            User customer,
            LocalDateTime now,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT s FROM CustomerSubscription s
           WHERE s.customer = :customer
             AND s.active = true
             AND s.expiresAt > :now
           ORDER BY s.startedAt DESC NULLS LAST, s.id DESC
           """)
    List<CustomerSubscription> findActiveSubscriptionsForUpdate(
            User customer,
            LocalDateTime now,
            Pageable pageable
    );

    default Optional<CustomerSubscription> findActiveSubscription(User customer, LocalDateTime now) {
        return findActiveSubscriptions(customer, now, PageRequest.of(0, 1)).stream().findFirst();
    }

    default Optional<CustomerSubscription> findActiveSubscriptionForUpdate(User customer, LocalDateTime now) {
        return findActiveSubscriptionsForUpdate(customer, now, PageRequest.of(0, 1)).stream().findFirst();
    }

    Optional<CustomerSubscription> findByPaymentId(String paymentId);

    @Modifying(clearAutomatically = true)
    void deleteByCustomerId(Long customerId);
}
