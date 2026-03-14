package com.casting.platform.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentWebhookRequest {

    @NotBlank
    private String externalId;

    @NotBlank
    private String status; // SUCCESS / FAILED

    private String providerPaymentId;
}
