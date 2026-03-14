package com.casting.platform.dto.request.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionPaymentRequest {

    @NotNull
    private Long planId;
}
