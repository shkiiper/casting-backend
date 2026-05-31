package com.casting.platform.service;

import com.casting.platform.dto.response.customer.SubscriptionInfoResponse;
import com.casting.platform.entity.CustomerSubscription;
import com.casting.platform.entity.CustomerSubscriptionPlan;
import com.casting.platform.entity.PromoCode;
import com.casting.platform.entity.User;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import com.casting.platform.repository.CustomerSubscriptionRepository;
import com.casting.platform.repository.PromoCodeRepository;
import com.casting.platform.repository.UserRepository;
import com.casting.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoCodeService {

    private static final String PROMO_PLAN_NAME = "Promo 30 days";

    private final PromoCodeRepository promoCodeRepository;
    private final CustomerSubscriptionRepository subscriptionRepository;
    private final CustomerSubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    public SubscriptionInfoResponse redeem(String rawCode) {
        String code = normalizeCode(rawCode);
        User customer = getCurrentCustomer();
        LocalDateTime now = LocalDateTime.now();

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid promo code"));

        validatePromoCode(promoCode, now);

        subscriptionRepository.findActiveSubscription(customer, now).ifPresent(subscription -> {
            throw new BadRequestException("Customer already has an active subscription");
        });

        CustomerSubscriptionPlan plan = getPromoPlan();
        CustomerSubscription subscription = new CustomerSubscription();
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setStartedAt(now);
        subscription.setExpiresAt(now.plusDays(promoCode.getDurationDays()));
        subscription.setTotalContactLimit(promoCode.getContactLimit());
        subscription.setUsedContacts(0);
        subscription.setBoosterCount(0);
        subscription.setPaymentId("promo_" + promoCode.getCode());
        subscription.setPaymentStatus("PROMO");
        subscription.setPaidAmount(BigDecimal.ZERO);
        subscription.setActive(true);
        subscriptionRepository.save(subscription);

        promoCode.setUsedByCustomer(customer);
        promoCode.setSubscription(subscription);
        promoCode.setUsedAt(now);
        promoCodeRepository.save(promoCode);

        customer.setCustomerSubscriptionActive(true);
        customer.setCustomerSubscriptionUntil(subscription.getExpiresAt());
        userRepository.save(customer);

        return toSubscriptionInfo(subscription);
    }

    private void validatePromoCode(PromoCode promoCode, LocalDateTime now) {
        if (!promoCode.isActive()) {
            throw new BadRequestException("Promo code is inactive");
        }
        if (promoCode.getUsedAt() != null) {
            throw new BadRequestException("Promo code has already been used");
        }
        if (promoCode.getExpiresAt() != null && !promoCode.getExpiresAt().isAfter(now)) {
            throw new BadRequestException("Promo code has expired");
        }
        if (promoCode.getContactLimit() <= 0) {
            throw new BadRequestException("Promo code contact limit is not configured");
        }
        if (promoCode.getDurationDays() <= 0) {
            throw new BadRequestException("Promo code duration is not configured");
        }
    }

    private CustomerSubscriptionPlan getPromoPlan() {
        return planRepository.findFirstByName(PROMO_PLAN_NAME)
                .orElseThrow(() -> new BadRequestException("Promo subscription plan is not configured"));
    }

    private SubscriptionInfoResponse toSubscriptionInfo(CustomerSubscription subscription) {
        SubscriptionInfoResponse info = new SubscriptionInfoResponse();
        info.setActive(true);
        info.setPlanName(subscription.getPlan().getName());
        info.setTotalLimit(subscription.getTotalContactLimit());
        info.setRemainingContacts(subscription.getTotalContactLimit() - subscription.getUsedContacts());
        info.setExpiresAt(subscription.getExpiresAt());
        info.setDaysRemaining(calculateDaysRemaining(subscription.getExpiresAt()));
        return info;
    }

    private long calculateDaysRemaining(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            return 0;
        }
        long seconds = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        if (seconds <= 0) {
            return 0;
        }
        return (seconds + 86_399) / 86_400;
    }

    private String normalizeCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new BadRequestException("Promo code is required");
        }
        return rawCode.trim().toUpperCase(Locale.ROOT);
    }

    private User getCurrentCustomer() {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipal principal = (UserPrincipal) principalObj;
        return userRepository.findById(principal.getId()).orElseThrow();
    }
}
