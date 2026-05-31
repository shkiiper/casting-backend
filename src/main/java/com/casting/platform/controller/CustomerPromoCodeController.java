package com.casting.platform.controller;

import com.casting.platform.dto.request.customer.RedeemPromoCodeRequest;
import com.casting.platform.dto.response.customer.SubscriptionInfoResponse;
import com.casting.platform.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/promo-codes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerPromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping("/redeem")
    public SubscriptionInfoResponse redeem(@Valid @RequestBody RedeemPromoCodeRequest request) {
        return promoCodeService.redeem(request.getCode());
    }
}
