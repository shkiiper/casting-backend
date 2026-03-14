package com.casting.platform.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentStatusResponse {
    private String externalId;
    private String type;
    private String status;
    private BigDecimal amount;
    private String providerPaymentId;
    private LocalDateTime completedAt;
}
