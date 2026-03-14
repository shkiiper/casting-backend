package com.casting.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
public class CustomerSubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private BigDecimal pricePerPeriod;
    private int periodDays;

    private int baseContactLimit = 40;
    private BigDecimal boosterPrice = new BigDecimal("500");
    private int boosterContacts = 10;

    private BigDecimal castingPostPrice = new BigDecimal("1000");
    private int castingPostDays = 14;

    private BigDecimal premiumProfilePrice = new BigDecimal("1500");
    private int premiumProfileDays = 30;

    private boolean active = true;
}
