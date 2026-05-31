package com.casting.platform.service;

import com.casting.platform.entity.CustomerSubscription;
import com.casting.platform.entity.CustomerSubscriptionPlan;
import com.casting.platform.entity.PromoCode;
import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import com.casting.platform.repository.CustomerSubscriptionRepository;
import com.casting.platform.repository.PromoCodeRepository;
import com.casting.platform.repository.UserRepository;
import com.casting.platform.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromoCodeServiceTest {

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private CustomerSubscriptionRepository subscriptionRepository;

    @Mock
    private CustomerSubscriptionPlanRepository planRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PromoCodeService promoCodeService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void redeemCreatesFreeSubscriptionAndMarksPromoUsed() {
        User customer = customer();
        CustomerSubscriptionPlan promoPlan = promoPlan();
        PromoCode promoCode = promoCode("CUST-FREE-0001");

        authenticate(customer.getId());
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(promoCodeRepository.findByCode("CUST-FREE-0001")).thenReturn(Optional.of(promoCode));
        when(subscriptionRepository.findActiveSubscription(any(User.class), any())).thenReturn(Optional.empty());
        when(planRepository.findFirstByName("Promo 30 days")).thenReturn(Optional.of(promoPlan));
        when(subscriptionRepository.save(any(CustomerSubscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = promoCodeService.redeem(" cust-free-0001 ");

        ArgumentCaptor<CustomerSubscription> subscriptionCaptor = ArgumentCaptor.forClass(CustomerSubscription.class);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());
        CustomerSubscription subscription = subscriptionCaptor.getValue();

        assertEquals(customer, subscription.getCustomer());
        assertEquals(promoPlan, subscription.getPlan());
        assertEquals(100, subscription.getTotalContactLimit());
        assertEquals(0, subscription.getUsedContacts());
        assertEquals("PROMO", subscription.getPaymentStatus());
        assertTrue(subscription.isActive());
        assertNotNull(subscription.getExpiresAt());

        assertEquals(customer, promoCode.getUsedByCustomer());
        assertEquals(subscription, promoCode.getSubscription());
        assertNotNull(promoCode.getUsedAt());
        assertTrue(customer.isCustomerSubscriptionActive());
        assertEquals(subscription.getExpiresAt(), customer.getCustomerSubscriptionUntil());

        assertTrue(response.isActive());
        assertEquals("Promo 30 days", response.getPlanName());
        assertEquals(100, response.getTotalLimit());
        assertEquals(100, response.getRemainingContacts());
        assertEquals(30, response.getDaysRemaining());
        assertEquals(subscription.getExpiresAt(), response.getExpiresAt());
    }

    @Test
    void redeemFailsWhenPromoAlreadyUsed() {
        User customer = customer();
        PromoCode promoCode = promoCode("CUST-FREE-0002");
        promoCode.setUsedAt(java.time.LocalDateTime.now());

        authenticate(customer.getId());
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(promoCodeRepository.findByCode("CUST-FREE-0002")).thenReturn(Optional.of(promoCode));

        assertThrows(BadRequestException.class, () -> promoCodeService.redeem("CUST-FREE-0002"));

        verify(subscriptionRepository, never()).save(any(CustomerSubscription.class));
    }

    private User customer() {
        User customer = new User();
        customer.setId(7L);
        customer.setEmail("customer@example.com");
        customer.setPasswordHash("hash");
        customer.setRole(UserRole.CUSTOMER);
        customer.setActive(true);
        return customer;
    }

    private CustomerSubscriptionPlan promoPlan() {
        CustomerSubscriptionPlan plan = new CustomerSubscriptionPlan();
        plan.setId(3L);
        plan.setName("Promo 30 days");
        return plan;
    }

    private PromoCode promoCode(String code) {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(code);
        promoCode.setActive(true);
        promoCode.setContactLimit(100);
        promoCode.setDurationDays(30);
        return promoCode;
    }

    private void authenticate(Long userId) {
        UserPrincipal principal = new UserPrincipal(
                userId,
                "customer@example.com",
                "hash",
                UserRole.CUSTOMER,
                true,
                false
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }
}
