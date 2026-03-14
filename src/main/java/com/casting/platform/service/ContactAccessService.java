package com.casting.platform.service;

import com.casting.platform.dto.response.customer.ViewedContactResponse;
import com.casting.platform.entity.ContactView;
import com.casting.platform.repository.ContactViewRepository;
import com.casting.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactAccessService {

    private final ContactViewRepository contactViewRepository;
    private final CustomerSubscriptionService subscriptionService;

    /**
     * Списание лимита при первом просмотре контактов профиля
     */
    public void ensureContactsViewed(Long profileId) {
        Long customerId = getCurrentUserId();

        boolean alreadyViewed = contactViewRepository
                .findByCustomerIdAndProfileId(customerId, profileId)
                .isPresent();

        if (!alreadyViewed) {
            subscriptionService.showContacts(profileId);
        }
    }

    /**
     * История просмотренных контактов (БЕЗ списаний)
     */
    public Page<ViewedContactResponse> getViewedContacts(Pageable pageable) {
        Long customerId = getCurrentUserId();

        return contactViewRepository
                .findByCustomerIdOrderByViewedAtDesc(customerId, pageable)
                .map(this::mapToResponse);
    }

    private ViewedContactResponse mapToResponse(ContactView view) {
        var profile = view.getProfile();

        ViewedContactResponse dto = new ViewedContactResponse();
        dto.setProfileId(profile.getId());
        dto.setProfileType(profile.getType().name());
        dto.setDisplayName(profile.getDisplayName());
        dto.setCity(profile.getCity());
        dto.setMainPhotoUrl(profile.getMainPhotoUrl());
        dto.setViewedAt(view.getViewedAt());

        return dto;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return ((UserPrincipal) principal).getId();
    }
}
