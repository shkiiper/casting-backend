package com.casting.platform.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminSubscriptionPlanRequest {

    @NotBlank
    private String name;

    private BigDecimal pricePerPeriod;
    @Min(1)
    private int periodDays;

    @Min(1)
    private int baseContactLimit;

    @Min(0)
    private BigDecimal boosterPrice;

    @Min(1)
    private int boosterContacts;

    @Min(0)
    private BigDecimal castingPostPrice;

    @Min(1)
    private int castingPostDays;

    @Min(0)
    private BigDecimal premiumProfilePrice;

    @Min(1)
    private int premiumProfileDays;

    private boolean active = true;
}
