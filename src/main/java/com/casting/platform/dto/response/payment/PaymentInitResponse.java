package com.casting.platform.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentInitResponse {
    private String externalId;
    private String url;
    private String status;
}
