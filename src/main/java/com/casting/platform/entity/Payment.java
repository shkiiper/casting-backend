package com.casting.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Внешний ID платежа в Freedom Pay (или mock)
    @Column(nullable = false, unique = true)
    private String externalId;

    // CUSTOMER, BOOSTER, CASTING_POST и т.п.
    @Column(nullable = false)
    private String type;

    // Связанный заказчик
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Сумма платежа
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "booster_count")
    private Integer boosterCount;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    // PENDING / SUCCESS / FAILED
    @Column(nullable = false)
    private String status;

    // Доп. данные (JSON или текст) — например, какой план/сколько бустеров
    @Column(columnDefinition = "TEXT")
    private String details;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.provider == null || this.provider.isBlank()) {
            this.provider = "MOCK";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
