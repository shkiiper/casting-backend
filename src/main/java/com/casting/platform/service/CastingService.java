package com.casting.platform.service;

import com.casting.platform.dto.common.PageResponse;
import com.casting.platform.dto.request.casting.CastingFilterRequest;
import com.casting.platform.dto.request.casting.CreateCastingRequest;
import com.casting.platform.dto.response.casting.CastingResponse;
import com.casting.platform.entity.CastingPost;
import com.casting.platform.entity.CustomerSubscriptionPlan;
import com.casting.platform.entity.User;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.exception.ForbiddenException;
import com.casting.platform.exception.NotFoundException;
import com.casting.platform.repository.CastingPostRepository;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import com.casting.platform.repository.UserRepository;
import com.casting.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CastingService {

    private final CastingPostRepository castingPostRepository;
    private final UserRepository userRepository;
    private final CustomerSubscriptionPlanRepository planRepository;

    private CustomerSubscriptionPlan getDefaultPlan() {
        return planRepository.findAll().stream()
                .filter(CustomerSubscriptionPlan::isActive)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No active subscription plan configured"));
    }

    public CastingResponse createCasting(CreateCastingRequest request) {
        throw new BadRequestException("Direct casting creation is disabled. Use paid creation flow.");
    }

    public PageResponse<CastingResponse> getMyCastings(Pageable pageable) {
        User customer = getCurrentUser();
        Page<CastingPost> page = castingPostRepository.findByCustomerOrderByCreatedAtDesc(customer, pageable);
        Page<CastingResponse> mapped = page.map(this::mapToResponse);

        return new PageResponse<>(
                mapped.getContent(),
                mapped.getNumber(),
                mapped.getSize(),
                mapped.getTotalElements(),
                mapped.getTotalPages(),
                mapped.isLast()
        );
    }

    public PageResponse<CastingResponse> getActiveCastings(CastingFilterRequest filters,
                                                           Pageable pageable) {

        Specification<CastingPost> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("active")));

            predicates.add(
                    cb.or(
                            cb.isNull(root.get("expiresAt")),
                            cb.greaterThan(root.get("expiresAt"), LocalDateTime.now())
                    )
            );

            if (filters.getCity() != null && !filters.getCity().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("city")),
                                "%" + filters.getCity().toLowerCase() + "%"
                        )
                );
            }

            if (filters.getProjectType() != null && !filters.getProjectType().isBlank()) {
                predicates.add(
                        cb.equal(
                                cb.lower(root.get("projectType")),
                                filters.getProjectType().toLowerCase()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<CastingPost> page = castingPostRepository.findAll(spec, pageable);

        Page<CastingResponse> mapped = page.map(this::mapToResponse);

        return new PageResponse<>(
                mapped.getContent(),
                mapped.getNumber(),
                mapped.getSize(),
                mapped.getTotalElements(),
                mapped.getTotalPages(),
                mapped.isLast()
        );
    }


    public CastingResponse getCasting(Long id) {
        CastingPost post = castingPostRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Casting not found"));
        return mapToResponse(post);
    }

    public void closeCasting(Long id) {
        User me = getCurrentUser();
        CastingPost post = castingPostRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Casting not found"));

        if (!post.getCustomer().getId().equals(me.getId())) {
            throw new ForbiddenException("You are not the owner of this casting");
        }

        post.setActive(false);
        post.setExpiresAt(LocalDateTime.now());
        castingPostRepository.save(post);
    }

    private CastingResponse mapToResponse(CastingPost post) {
        CastingResponse dto = new CastingResponse();
        dto.setId(post.getId());
        dto.setCustomerId(post.getCustomer().getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setCity(post.getCity());
        dto.setProjectType(post.getProjectType());
        dto.setPublishedAt(post.getPublishedAt());
        dto.setExpiresAt(post.getExpiresAt());
        dto.setActive(post.isActive());
        return dto;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ForbiddenException("No authenticated user");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private String emptyToNull(String v) {
        return (v == null || v.isBlank()) ? null : v;
    }
}
