package com.casting.platform.dto.request.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RedeemPromoCodeRequest {

    @NotBlank
    private String code;
}
