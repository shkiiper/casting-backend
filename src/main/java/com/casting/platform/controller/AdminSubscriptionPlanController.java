package com.casting.platform.controller;

import com.casting.platform.dto.request.admin.AdminPlanBasicsRequest;
import com.casting.platform.dto.request.admin.AdminPlanBoosterRequest;
import com.casting.platform.dto.request.admin.AdminPlanCastingRequest;
import com.casting.platform.dto.request.admin.AdminPlanPremiumRequest;
import com.casting.platform.dto.request.admin.AdminSubscriptionPlanRequest;
import com.casting.platform.dto.response.admin.AdminSubscriptionPlanResponse;
import com.casting.platform.service.AdminSubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionPlanController {

    private final AdminSubscriptionPlanService planService;

    @GetMapping
    public ResponseEntity<List<AdminSubscriptionPlanResponse>> getAll() {
        return ResponseEntity.ok(planService.getAll());
    }

    @PostMapping
    public ResponseEntity<AdminSubscriptionPlanResponse> create(
            @Valid @RequestBody AdminSubscriptionPlanRequest request) {
        return ResponseEntity.ok(planService.create(request));
    }

    @PostMapping("/basics")
    public ResponseEntity<AdminSubscriptionPlanResponse> createBasics(
            @Valid @RequestBody AdminPlanBasicsRequest request) {
        return ResponseEntity.ok(planService.createBasics(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminSubscriptionPlanResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminSubscriptionPlanRequest request) {
        return ResponseEntity.ok(planService.update(id, request));
    }

    @PutMapping("/{id}/basics")
    public ResponseEntity<AdminSubscriptionPlanResponse> updateBasics(
            @PathVariable Long id,
            @Valid @RequestBody AdminPlanBasicsRequest request) {
        return ResponseEntity.ok(planService.updateBasics(id, request));
    }

    @PutMapping("/{id}/booster")
    public ResponseEntity<AdminSubscriptionPlanResponse> updateBooster(
            @PathVariable Long id,
            @Valid @RequestBody AdminPlanBoosterRequest request) {
        return ResponseEntity.ok(planService.updateBooster(id, request));
    }

    @PutMapping("/{id}/casting")
    public ResponseEntity<AdminSubscriptionPlanResponse> updateCasting(
            @PathVariable Long id,
            @Valid @RequestBody AdminPlanCastingRequest request) {
        return ResponseEntity.ok(planService.updateCasting(id, request));
    }

    @PutMapping("/{id}/premium")
    public ResponseEntity<AdminSubscriptionPlanResponse> updatePremium(
            @PathVariable Long id,
            @Valid @RequestBody AdminPlanPremiumRequest request) {
        return ResponseEntity.ok(planService.updatePremium(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
