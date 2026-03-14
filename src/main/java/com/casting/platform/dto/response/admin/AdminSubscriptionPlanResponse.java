package com.casting.platform.dto.response.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminSubscriptionPlanResponse {

    private Long id;
    private String name;
    private BigDecimal pricePerPeriod;
    private int periodDays;
    private int baseContactLimit;
    private BigDecimal boosterPrice;
    private int boosterContacts;
    private BigDecimal castingPostPrice;
    private int castingPostDays;
    private BigDecimal premiumProfilePrice;
    private int premiumProfileDays;
    private boolean active;
}
