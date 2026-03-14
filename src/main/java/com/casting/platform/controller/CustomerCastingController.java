package com.casting.platform.controller;

import com.casting.platform.dto.request.casting.CastingPaymentRequest;
import com.casting.platform.dto.response.payment.PaymentInitResponse;
import com.casting.platform.service.CustomerSubscriptionService;
import com.casting.platform.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/castings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerCastingController {

    private final PaymentService paymentService;
    private final CustomerSubscriptionService subscriptionService;

    // Инициализация платного объявления (создаётся после webhook)
    @PostMapping("/pay")
    public ResponseEntity<PaymentInitResponse> initCastingPayment(
            @Valid @RequestBody CastingPaymentRequest request) {

        Long customerId = subscriptionService.getCurrentCustomerId();
        PaymentInitResponse resp = paymentService.initCastingPayment(customerId, request);
        return ResponseEntity.ok(resp);
    }
}
