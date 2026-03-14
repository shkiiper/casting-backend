package com.casting.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_subscriptions")
@Getter
@Setter
public class CustomerSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private CustomerSubscriptionPlan plan;

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime expiresAt;

    private int totalContactLimit;     // base + бустеры
    private int usedContacts;          // расход за период

    private int boosterCount;          // купленные бустеры

    // Платежи
    private String paymentId;          // ID транзакции Freedom Pay
    private String paymentStatus;      // PENDING/SUCCESS/FAILED
    private BigDecimal paidAmount;

    private boolean active = true;
}
