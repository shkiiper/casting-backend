package com.casting.platform.controller;

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

    @PutMapping("/{id}")
    public ResponseEntity<AdminSubscriptionPlanResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminSubscriptionPlanRequest request) {
        return ResponseEntity.ok(planService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
