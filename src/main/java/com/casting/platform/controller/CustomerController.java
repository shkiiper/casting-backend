package com.casting.platform.controller;

import com.casting.platform.dto.request.customer.UpdateCustomerProfileRequest;
import com.casting.platform.dto.response.customer.CustomerPlanResponse;
import com.casting.platform.dto.response.customer.CustomerProfileResponse;
import com.casting.platform.dto.response.customer.SubscriptionInfoResponse;
import com.casting.platform.service.CustomerPlanService;
import com.casting.platform.service.CustomerSubscriptionService;
import com.casting.platform.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    private final ProfileService profileService;
    private final CustomerPlanService customerPlanService;
    private final CustomerSubscriptionService subscriptionService;

    @GetMapping("/me")
    public CustomerProfileResponse me() {
        return profileService.getMyCustomerProfile();
    }

    @PatchMapping("/me")
    public CustomerProfileResponse updateMe(
            @RequestBody UpdateCustomerProfileRequest request
    ) {
        return profileService.updateCustomerProfile(request);
    }

    @GetMapping("/subscription")
    public SubscriptionInfoResponse subscription() {
        return subscriptionService.getCurrentSubscriptionInfo();
    }

    @GetMapping("/plans")
    public List<CustomerPlanResponse> plans() {
        return customerPlanService.getActivePlans();
    }
}