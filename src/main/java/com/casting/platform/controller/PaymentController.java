package com.casting.platform.controller;

import com.casting.platform.dto.request.casting.CastingPaymentRequest;
import com.casting.platform.dto.request.payment.PaymentWebhookRequest;
import com.casting.platform.dto.response.payment.PaymentInitResponse;
import com.casting.platform.dto.response.payment.PaymentStatusResponse;
import com.casting.platform.exception.ForbiddenException;
import com.casting.platform.security.UserPrincipal;
import com.casting.platform.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Инициация оплаты подписки для текущего пользователя
    @PostMapping("/subscription/{planId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentInitResponse initSubscriptionPayment(@PathVariable Long planId) {
        Long customerId = getCurrentUserId();
        return paymentService.initSubscriptionPayment(customerId, planId);
    }

    // Инициация оплаты бустеров контактов
    @PostMapping("/booster/{planId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentInitResponse initBoosterPayment(@PathVariable Long planId,
                                                  @RequestParam(defaultValue = "1") int boosterCount) {
        Long customerId = getCurrentUserId();
        return paymentService.initBoosterPayment(customerId, planId, boosterCount);
    }

    // Инициация оплаты за кастинг-пост
    @PostMapping("/casting")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentInitResponse initCastingPayment(@Valid @RequestBody CastingPaymentRequest request) {
        Long customerId = getCurrentUserId();
        return paymentService.initCastingPayment(customerId, request);
    }

    @PostMapping("/profile-premium")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentInitResponse initProfilePremiumPayment() {
        Long userId = getCurrentUserId();
        return paymentService.initPerformerPremiumPayment(userId);
    }

    // Вебхук от платёжного провайдера (может быть без аутентификации пользователя)
    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.OK)
    public void handleWebhook(@Valid @RequestBody PaymentWebhookRequest request,
                              @RequestHeader("X-Signature") String signature) {
        paymentService.handleWebhook(request, signature);
    }

    @GetMapping("/{externalId}")
    public PaymentStatusResponse getPaymentStatus(@PathVariable String externalId) {
        UserPrincipal principal = getCurrentUserPrincipal();
        return paymentService.getPaymentStatus(externalId, principal.getId(), principal.getRole());
    }

    private Long getCurrentUserId() {
        return getCurrentUserPrincipal().getId();
    }

    private UserPrincipal getCurrentUserPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ForbiddenException("No authenticated user");
        }
        return principal;
    }
}
