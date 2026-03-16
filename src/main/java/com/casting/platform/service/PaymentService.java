package com.casting.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.casting.platform.dto.request.casting.CastingPaymentRequest;
import com.casting.platform.dto.request.payment.PaymentWebhookRequest;
import com.casting.platform.dto.response.payment.PaymentInitResponse;
import com.casting.platform.dto.response.payment.PaymentStatusResponse;
import com.casting.platform.entity.CastingPost;
import com.casting.platform.entity.CustomerSubscription;
import com.casting.platform.entity.CustomerSubscriptionPlan;
import com.casting.platform.entity.Payment;
import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.exception.ForbiddenException;
import com.casting.platform.exception.NotFoundException;
import com.casting.platform.repository.CastingPostRepository;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import com.casting.platform.repository.CustomerSubscriptionRepository;
import com.casting.platform.repository.PaymentRepository;
import com.casting.platform.repository.PerformerProfileRepository;
import com.casting.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CustomerSubscriptionPlanRepository planRepository;
    private final CustomerSubscriptionRepository subscriptionRepository;
    private final CastingPostRepository castingPostRepository;
    private final PerformerProfileRepository performerProfileRepository;
    private final WebhookSignatureVerifier webhookSignatureVerifier;
    private final ObjectMapper objectMapper;

    @Value("${app.publicUrl:http://localhost:8080}")
    private String publicUrl;

    public PaymentInitResponse initSubscriptionPayment(Long customerId, Long planId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        CustomerSubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        if (plan.getPricePerPeriod() == null) {
            throw new BadRequestException("Subscription plan price is not configured");
        }

        String externalId = "sub_" + UUID.randomUUID();

        Payment payment = new Payment();
        payment.setExternalId(externalId);
        payment.setType("SUBSCRIPTION");
        payment.setCustomer(customer);
        payment.setAmount(plan.getPricePerPeriod());
        payment.setPlanId(planId);
        payment.setStatus("PENDING");
        payment.setProvider("MOCK");
        paymentRepository.save(payment);

        String url = publicUrl + "/mock-pay/" + externalId;
        return new PaymentInitResponse(externalId, url, "PENDING");
    }

    public PaymentInitResponse initBoosterPayment(Long customerId, Long planId, int boosterCount) {
        if (boosterCount <= 0) {
            throw new BadRequestException("boosterCount must be greater than 0");
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        CustomerSubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        if (plan.getBoosterPrice() == null) {
            throw new BadRequestException("Booster price is not configured");
        }

        String externalId = "boost_" + UUID.randomUUID();

        BigDecimal amount = plan.getBoosterPrice().multiply(BigDecimal.valueOf(boosterCount));

        Payment payment = new Payment();
        payment.setExternalId(externalId);
        payment.setType("BOOSTER");
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setPlanId(planId);
        payment.setBoosterCount(boosterCount);
        payment.setStatus("PENDING");
        payment.setProvider("MOCK");
        paymentRepository.save(payment);

        String url = publicUrl + "/mock-pay/" + externalId;
        return new PaymentInitResponse(externalId, url, "PENDING");
    }

    public PaymentInitResponse initCastingPayment(Long customerId, CastingPaymentRequest req) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        CustomerSubscriptionPlan plan = getActivePlan();

        if (plan.getCastingPostPrice() == null) {
            throw new BadRequestException("Casting post daily price is not configured");
        }

        String externalId = "cast_" + UUID.randomUUID();
        BigDecimal amount = plan.getCastingPostPrice().multiply(BigDecimal.valueOf(req.getDays()));

        Payment payment = new Payment();
        payment.setExternalId(externalId);
        payment.setType("CASTING_POST");
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setProvider("MOCK");
        payment.setDetails(writeDetails(Map.of(
                "title", req.getTitle(),
                "description", req.getDescription(),
                "city", req.getCity() == null ? "" : req.getCity(),
                "projectType", req.getProjectType() == null ? "" : req.getProjectType(),
                "days", req.getDays(),
                "pricePerDay", plan.getCastingPostPrice()
        )));

        paymentRepository.save(payment);

        String url = publicUrl + "/mock-pay/" + externalId;
        return new PaymentInitResponse(externalId, url, "PENDING");
    }

    public PaymentInitResponse initPerformerPremiumPayment(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        validatePerformerRole(user);

        PerformerProfile profile = performerProfileRepository.findByOwnerId(userId)
                .orElseThrow(() -> new BadRequestException("Create performer profile before buying premium"));
        CustomerSubscriptionPlan plan = getActivePlan();

        if (plan.getPremiumProfilePrice() == null) {
            throw new BadRequestException("Premium profile price is not configured");
        }
        if (plan.getPremiumProfilePrice().signum() < 0) {
            throw new BadRequestException("Premium profile price cannot be negative");
        }
        if (plan.getPremiumProfileDays() <= 0) {
            throw new BadRequestException("Premium profile days must be greater than 0");
        }

        String externalId = "prem_" + UUID.randomUUID();

        Payment payment = new Payment();
        payment.setExternalId(externalId);
        payment.setType("PROFILE_PREMIUM");
        payment.setCustomer(user);
        payment.setAmount(plan.getPremiumProfilePrice());
        payment.setStatus("PENDING");
        payment.setProvider("MOCK");
        payment.setDetails(writeDetails(Map.of(
                "profileId", profile.getId(),
                "performerType", profile.getType().name(),
                "premiumDays", plan.getPremiumProfileDays()
        )));

        paymentRepository.save(payment);

        String url = publicUrl + "/mock-pay/" + externalId;
        return new PaymentInitResponse(externalId, url, "PENDING");
    }

    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(String externalId, Long requesterId, UserRole requesterRole) {
        Payment payment = paymentRepository.findByExternalId(externalId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        boolean isOwner = payment.getCustomer() != null && payment.getCustomer().getId().equals(requesterId);
        boolean isAdmin = requesterRole == UserRole.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Payment is not available for current user");
        }

        return new PaymentStatusResponse(
                payment.getExternalId(),
                payment.getType(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getProviderPaymentId(),
                payment.getCompletedAt()
        );
    }

    public void handleWebhook(PaymentWebhookRequest request, String signature) {
        validateWebhookRequest(request);

        String payload = buildWebhookPayload(
                request.getExternalId(),
                request.getStatus(),
                request.getProviderPaymentId()
        );
        webhookSignatureVerifier.verify(payload, signature);

        processWebhook(request);
    }

    public void processMockWebhook(String externalId, String status) {
        String normalizedStatus = normalizeStatus(status);
        PaymentWebhookRequest request = new PaymentWebhookRequest();
        request.setExternalId(externalId);
        request.setStatus(normalizedStatus);
        request.setProviderPaymentId("mock_" + UUID.randomUUID());

        String payload = buildWebhookPayload(
                request.getExternalId(),
                request.getStatus(),
                request.getProviderPaymentId()
        );
        String signature = webhookSignatureVerifier.sign(payload);

        handleWebhook(request, signature);
    }

    private void processWebhook(PaymentWebhookRequest request) {
        Payment payment = paymentRepository.findByExternalIdForUpdate(request.getExternalId())
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (!"PENDING".equals(payment.getStatus())) {
            return;
        }

        if (!"SUCCESS".equalsIgnoreCase(request.getStatus())) {
            payment.setStatus("FAILED");
            payment.setProviderPaymentId(request.getProviderPaymentId());
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            return;
        }

        payment.setStatus("SUCCESS");
        payment.setProviderPaymentId(request.getProviderPaymentId());
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        switch (payment.getType()) {
            case "SUBSCRIPTION" -> activateSubscription(payment);
            case "BOOSTER" -> applyBooster(payment);
            case "CASTING_POST" -> createCastingPostAfterPayment(payment);
            case "PROFILE_PREMIUM" -> activatePerformerPremium(payment);
            default -> throw new BadRequestException("Unsupported payment type: " + payment.getType());
        }
    }

    private void activateSubscription(Payment payment) {
        User customer = payment.getCustomer();

        Long planId = payment.getPlanId();
        if (planId == null) {
            throw new BadRequestException("Subscription payment has no planId");
        }

        CustomerSubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        LocalDateTime now = LocalDateTime.now();
        subscriptionRepository.findActiveSubscription(customer, now).ifPresent(existing -> {
            existing.setActive(false);
            subscriptionRepository.save(existing);
        });

        CustomerSubscription subscription = new CustomerSubscription();
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setStartedAt(now);
        subscription.setExpiresAt(now.plusDays(plan.getPeriodDays()));
        subscription.setTotalContactLimit(plan.getBaseContactLimit());
        subscription.setUsedContacts(0);
        subscription.setBoosterCount(0);
        subscription.setPaymentId(payment.getExternalId());
        subscription.setPaymentStatus("SUCCESS");
        subscription.setPaidAmount(payment.getAmount());
        subscription.setActive(true);

        subscriptionRepository.save(subscription);

        customer.setCustomerSubscriptionActive(true);
        customer.setCustomerSubscriptionUntil(subscription.getExpiresAt());
        userRepository.save(customer);
    }

    private void applyBooster(Payment payment) {
        User customer = payment.getCustomer();
        CustomerSubscription subscription = subscriptionRepository
                .findActiveSubscription(customer, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("No active subscription"));

        CustomerSubscriptionPlan plan = subscription.getPlan();
        int boosterCount = payment.getBoosterCount() != null ? payment.getBoosterCount() : 1;
        int extraContacts = plan.getBoosterContacts() * boosterCount;

        subscription.setTotalContactLimit(subscription.getTotalContactLimit() + extraContacts);
        subscription.setBoosterCount(subscription.getBoosterCount() + boosterCount);
        subscriptionRepository.save(subscription);
    }

    private void createCastingPostAfterPayment(Payment payment) {
        Map<String, Object> details = readDetails(payment.getDetails());
        User customer = payment.getCustomer();
        Integer days = toInteger(details.get("days"));
        if (days == null || days <= 0) {
            throw new BadRequestException("Casting payment details are corrupted");
        }

        CastingPost post = new CastingPost();
        post.setCustomer(customer);
        post.setTitle((String) details.get("title"));
        post.setDescription((String) details.get("description"));
        post.setCity(blankToNull((String) details.get("city")));
        post.setProjectType(blankToNull((String) details.get("projectType")));
        post.setPublishedAt(LocalDateTime.now());
        post.setExpiresAt(LocalDateTime.now().plusDays(days));
        post.setActive(true);

        castingPostRepository.save(post);
    }

    private void activatePerformerPremium(Payment payment) {
        Map<String, Object> details = readDetails(payment.getDetails());
        Long profileId = toLong(details.get("profileId"));
        Integer premiumDays = toInteger(details.get("premiumDays"));
        if (profileId == null || premiumDays == null || premiumDays <= 0) {
            throw new BadRequestException("Premium payment details are corrupted");
        }

        var profile = performerProfileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        if (!profile.getOwner().getId().equals(payment.getCustomer().getId())) {
            throw new ForbiddenException("Premium payment does not belong to this profile");
        }

        LocalDateTime base = profile.getPremiumUntil() != null && profile.getPremiumUntil().isAfter(LocalDateTime.now())
                ? profile.getPremiumUntil()
                : LocalDateTime.now();
        if (profile.getPremiumSince() == null || profile.getPremiumUntil() == null || profile.getPremiumUntil().isBefore(LocalDateTime.now())) {
            profile.setPremiumSince(LocalDateTime.now());
        }
        LocalDateTime premiumUntil = base.plusDays(premiumDays);
        profile.setPremiumUntil(premiumUntil);
        performerProfileRepository.save(profile);

        User owner = profile.getOwner();
        owner.setPremium(true);
        owner.setPremiumUntil(premiumUntil);
        userRepository.save(owner);
    }

    private void validateWebhookRequest(PaymentWebhookRequest request) {
        if (request.getExternalId() == null || request.getExternalId().isBlank()) {
            throw new BadRequestException("externalId is required");
        }
        request.setStatus(normalizeStatus(request.getStatus()));
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("status is required");
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!"SUCCESS".equals(normalized) && !"FAILED".equals(normalized)) {
            throw new BadRequestException("status must be SUCCESS or FAILED");
        }
        return normalized;
    }

    public String buildWebhookPayload(String externalId, String status, String providerPaymentId) {
        return externalId + "|" + status + "|" + (providerPaymentId == null ? "" : providerPaymentId);
    }

    private CustomerSubscriptionPlan getActivePlan() {
        List<CustomerSubscriptionPlan> activePlans = planRepository.findByActiveTrueOrderByIdAsc();
        if (activePlans.isEmpty()) {
            throw new BadRequestException("No active subscription plan configured");
        }
        if (activePlans.size() > 1) {
            throw new BadRequestException("Multiple active subscription plans configured");
        }
        return activePlans.get(0);
    }

    private void validatePerformerRole(User user) {
        if (user.getRole() != UserRole.ACTOR
                && user.getRole() != UserRole.CREATOR
                && user.getRole() != UserRole.LOCATION_OWNER) {
            throw new ForbiddenException("Only performers can buy profile premium");
        }
    }

    private String writeDetails(Map<String, Object> details) {
        try {
            return objectMapper.writeValueAsString(new LinkedHashMap<>(details));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payment details", e);
        }
    }

    private Map<String, Object> readDetails(String details) {
        if (details == null || details.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(details, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Payment details are corrupted");
        }
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String string && !string.isBlank()) {
            return Integer.parseInt(string);
        }
        return null;
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String string && !string.isBlank()) {
            return Long.parseLong(string);
        }
        return null;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
