package com.casting.platform.controller;

import com.casting.platform.dto.response.customer.ViewedContactResponse;
import com.casting.platform.service.ContactAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/contacts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerContactsController {

    private final ContactAccessService contactAccessService;

    /**
     * История просмотренных контактов (кабинет заказчика)
     */
    @GetMapping("/viewed")
    public Page<ViewedContactResponse> viewedHistory(Pageable pageable) {
        return contactAccessService.getViewedContacts(pageable);
    }

    /**
     * Списание лимита при открытии профиля
     */
    @PostMapping("/viewed")
    public ResponseEntity<Void> viewed(@RequestParam Long profileId) {
        contactAccessService.ensureContactsViewed(profileId);
        return ResponseEntity.ok().build();
    }
}
