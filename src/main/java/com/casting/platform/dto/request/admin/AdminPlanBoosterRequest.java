package com.casting.platform.dto.request.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminPlanBoosterRequest {

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal boosterPrice;

    @Min(1)
    private int boosterContacts;
}
