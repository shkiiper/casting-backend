package com.casting.platform.service;

import com.casting.platform.dto.common.PageResponse;
import com.casting.platform.dto.response.customer.ContactInfoResponse;
import com.casting.platform.dto.response.customer.SubscriptionInfoResponse;
import com.casting.platform.dto.response.customer.ViewedContactResponse;
import com.casting.platform.entity.ContactView;
import com.casting.platform.entity.CustomerSubscription;
import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.User;
import com.casting.platform.exception.LimitExceededException;
import com.casting.platform.exception.NotFoundException;
import com.casting.platform.repository.ContactViewRepository;
import com.casting.platform.repository.CustomerSubscriptionRepository;
import com.casting.platform.repository.PerformerProfileRepository;
import com.casting.platform.repository.UserRepository;
import com.casting.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerSubscriptionService {

    private final UserRepository userRepository;
    private final CustomerSubscriptionRepository subscriptionRepository;
    private final ContactViewRepository contactViewRepository;
    private final PerformerProfileRepository profileRepository;

    public SubscriptionInfoResponse getCurrentSubscriptionInfo() {
        User customer = getCurrentCustomer();

        CustomerSubscription subscription = subscriptionRepository
                .findActiveSubscription(customer, LocalDateTime.now())
                .orElse(null);

        SubscriptionInfoResponse info = new SubscriptionInfoResponse();
        if (subscription == null) {
            info.setActive(false);
            return info;
        }

        info.setActive(true);
        info.setPlanName(subscription.getPlan().getName());
        info.setTotalLimit(subscription.getTotalContactLimit());
        info.setRemainingContacts(subscription.getTotalContactLimit() - subscription.getUsedContacts());
        return info;
    }

    /**
     * Кнопка "Показать контакты":
     * - если уже смотрел: не списываем
     * - если новый просмотр: списываем и фиксируем
     * - возвращаем контакты (только при активной подписке)
     */
    public ContactInfoResponse showContacts(Long profileId) {
        System.out.println("showContacts() called, profileId = " + profileId);

        User customer = getCurrentCustomer();
        System.out.println("customer id = " + customer.getId());

        CustomerSubscription subscription = getActiveSubscription(customer);
        System.out.println("subscription id = " + subscription.getId());

        PerformerProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        System.out.println("profile id = " + profile.getId());

        boolean alreadyViewed = contactViewRepository
                .findByCustomerIdAndProfileId(customer.getId(), profileId)
                .isPresent();
        System.out.println("alreadyViewed = " + alreadyViewed);

        if (!alreadyViewed) {
            if (subscription.getUsedContacts() >= subscription.getTotalContactLimit()) {
                throw new LimitExceededException("Contact limit exceeded");
            }

            subscription.setUsedContacts(subscription.getUsedContacts() + 1);
            subscriptionRepository.save(subscription);
            System.out.println("usedContacts incremented to " + subscription.getUsedContacts());

            ContactView view = new ContactView();
            view.setCustomer(customer);
            view.setProfile(profile);
            contactViewRepository.save(view);
            System.out.println("ContactView saved");
        }

        return new ContactInfoResponse(
                profile.getContactPhone(),
                profile.getContactEmail(),
                profile.getContactWhatsapp(),
                profile.getContactTelegram()
        );
    }


    /**
     * История просмотренных контактов: доступна только при активной подписке.
     * (По ТЗ после окончания подписки даже ранее просмотренные контакты скрываются)
     */
    public PageResponse<ViewedContactResponse> getViewedContacts(Pageable pageable) {
        User customer = getCurrentCustomer();
        getActiveSubscription(customer); // просто проверка

        Page<ContactView> page = contactViewRepository
                .findByCustomerIdOrderByViewedAtDesc(customer.getId(), pageable);

        Page<ViewedContactResponse> mapped = page.map(cv -> {
            PerformerProfile p = cv.getProfile();

            ViewedContactResponse dto = new ViewedContactResponse();
            dto.setProfileId(p.getId());
            dto.setProfileType(p.getType().name());
            dto.setCity(p.getCity());
            dto.setMainPhotoUrl(p.getMainPhotoUrl());
            dto.setViewedAt(cv.getViewedAt());

            // минимальное отображаемое имя
            String displayName = p.getDisplayName();
            if (displayName == null || displayName.isBlank()) {
                String fn = p.getFirstName() == null ? "" : p.getFirstName();
                String ln = p.getLastName() == null ? "" : p.getLastName();
                String combined = (fn + " " + ln).trim();
                displayName = combined.isBlank() ? p.getLocationName() : combined;
            }
            dto.setDisplayName(displayName);

            return dto;
        });

        return new PageResponse<>(
                mapped.getContent(),
                mapped.getNumber(),
                mapped.getSize(),
                mapped.getTotalElements(),
                mapped.getTotalPages(),
                mapped.isLast()
        );
    }

    private CustomerSubscription getActiveSubscription(User customer) {
        return subscriptionRepository.findActiveSubscription(customer, LocalDateTime.now())
                .orElseThrow(() -> new LimitExceededException("No active subscription"));
    }


    private User getCurrentCustomer() {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipal principal = (UserPrincipal) principalObj;
        return userRepository.findById(principal.getId()).orElseThrow();
    }
    public Long getCurrentCustomerId() {
        Object principalObj = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ((UserPrincipal) principalObj).getId();
    }

}
