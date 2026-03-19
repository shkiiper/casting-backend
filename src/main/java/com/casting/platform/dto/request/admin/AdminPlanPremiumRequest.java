package com.casting.platform.dto.request.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminPlanPremiumRequest {

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal premiumProfilePrice;

    @Min(1)
    private int premiumProfileDays;
}
