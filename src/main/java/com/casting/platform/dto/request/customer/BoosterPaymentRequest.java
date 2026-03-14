package com.casting.platform.dto.request.customer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoosterPaymentRequest {

    @NotNull
    private Long planId; // по какому тарифу считаем цену бустера

    @Min(1)
    private int boosterCount = 1; // сколько бустеров покупаем (1 бустер = 10 контактов)
}
