package com.casting.platform.dto.request.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminPlanBasicsRequest {

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal pricePerPeriod;

    @Min(1)
    private int periodDays;

    @Min(1)
    private int baseContactLimit;

    private boolean active = true;
}
