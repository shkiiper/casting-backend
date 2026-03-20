package com.casting.platform.service;

import com.casting.platform.dto.common.PageResponse;
import com.casting.platform.dto.response.admin.AdminUserResponse;
import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.exception.NotFoundException;
import com.casting.platform.repository.CastingPostRepository;
import com.casting.platform.repository.ContactViewRepository;
import com.casting.platform.repository.CustomerSubscriptionRepository;
import com.casting.platform.repository.EmailVerificationTokenRepository;
import com.casting.platform.repository.PasswordResetTokenRepository;
import com.casting.platform.repository.PaymentRepository;
import com.casting.platform.repository.PerformerProfileRepository;
import com.casting.platform.repository.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final PerformerProfileRepository performerProfileRepository;
    private final ContactViewRepository contactViewRepository;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final CastingPostRepository castingPostRepository;
    private final PaymentRepository paymentRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> getUsers(
            Integer page,
            Integer size,
            String role,
            String query,
            String sortBy,
            String sortDir
    ) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : Math.min(size, 100);
        Sort sort = Sort.by(parseDirection(sortDir), mapSortProperty(sortBy));

        Specification<User> spec = buildSpec(role, query);
        Page<User> result = userRepository.findAll(spec, PageRequest.of(safePage, safeSize, sort));

        PageResponse<AdminUserResponse> response = new PageResponse<>();
        response.setContent(result.getContent().stream().map(this::toResponse).toList());
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setLast(result.isLast());
        return response;
    }

    public void deactivateUser(Long userId) {
        User user = getUserOrThrow(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        User user = getUserOrThrow(userId);
        user.setActive(true);
        userRepository.save(user);
    }

    public void banUser(Long userId) {
        User user = getUserOrThrow(userId);
        user.setBanned(true);
        userRepository.save(user);
    }

    public void unbanUser(Long userId) {
        User user = getUserOrThrow(userId);
        user.setBanned(false);
        userRepository.save(user);
    }

    public void updateProfileVisibility(Long userId, boolean published) {
        User user = getUserOrThrow(userId);
        PerformerProfile profile = user.getPerformerProfile();

        if (profile == null) {
            throw new NotFoundException("Profile not found");
        }

        if (published) {
            validateProfileBeforePublish(profile);
        }

        profile.setPublished(published);
        performerProfileRepository.save(profile);
    }

    public void deleteUser(Long userId) {
        User user = getUserOrThrow(userId);
        PerformerProfile profile = user.getPerformerProfile();
        Long profileId = profile != null ? profile.getId() : null;

        if (profileId != null) {
            contactViewRepository.deleteByProfileId(profileId);
        }

        emailVerificationTokenRepository.deleteByUserId(userId);
        passwordResetTokenRepository.deleteByUserId(userId);
        paymentRepository.deleteByCustomerId(userId);
        customerSubscriptionRepository.deleteByCustomerId(userId);
        castingPostRepository.deleteByCustomerId(userId);
        contactViewRepository.deleteByCustomerId(userId);

        if (profile != null) {
            performerProfileRepository.delete(profile);
            user.setPerformerProfile(null);
        }

        userRepository.flush();
        userRepository.delete(user);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Specification<User> buildSpec(String role, String query) {
        return (root, cq, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (role != null && !role.isBlank()) {
                try {
                    predicates.add(cb.equal(root.get("role"), UserRole.valueOf(role.trim().toUpperCase(Locale.ROOT))));
                } catch (IllegalArgumentException ex) {
                    throw new BadRequestException("Invalid role filter");
                }
            }

            if (query != null && !query.isBlank()) {
                String q = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                Join<User, PerformerProfile> profileJoin = root.join("performerProfile", JoinType.LEFT);
                List<jakarta.persistence.criteria.Predicate> orPredicates = new ArrayList<>();
                orPredicates.add(cb.like(cb.lower(root.get("email")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(root.get("phone"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(root.get("city"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(root.get("description"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(root.get("telegram"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(profileJoin.get("firstName"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(profileJoin.get("lastName"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(profileJoin.get("displayName"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(profileJoin.get("contactPhone"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(profileJoin.get("contactEmail"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(profileJoin.get("contactTelegram"), "")), q));
                orPredicates.add(cb.like(cb.lower(cb.coalesce(profileJoin.get("contactWhatsapp"), "")), q));

                try {
                    Long id = Long.parseLong(query.trim());
                    orPredicates.add(cb.equal(root.get("id"), id));
                } catch (NumberFormatException ignored) {
                    // non numeric query
                }

                predicates.add(cb.or(orPredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private Sort.Direction parseDirection(String sortDir) {
        if ("asc".equalsIgnoreCase(sortDir)) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }

    private String mapSortProperty(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }
        return switch (sortBy) {
            case "id", "email", "role", "createdAt", "updatedAt" -> sortBy;
            default -> "createdAt";
        };
    }

    private AdminUserResponse toResponse(User user) {
        AdminUserResponse r = new AdminUserResponse();
        PerformerProfile p = user.getPerformerProfile();

        r.setId(user.getId());
        r.setRole(user.getRole().name());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setCity(user.getCity());
        r.setDescription(firstNonBlank(user.getDescription(), p != null ? p.getDescription() : null, p != null ? p.getBio() : null));
        r.setTelegram(user.getTelegram());

        if (p != null) {
            r.setFirstName(p.getFirstName());
            r.setLastName(p.getLastName());
            r.setDisplayName(p.getDisplayName());
            r.setContactPhone(firstNonBlank(p.getContactPhone(), user.getPhone()));
            r.setContactEmail(firstNonBlank(p.getContactEmail(), user.getEmail()));
            r.setContactTelegram(firstNonBlank(p.getContactTelegram(), user.getTelegram()));
            r.setContactWhatsapp(p.getContactWhatsapp());
            r.setMinRate(p.getMinRate() != null ? p.getMinRate() : p.getRentPrice());
            r.setRateUnit(p.getRateUnit() != null ? p.getRateUnit() : p.getRentPriceUnit());
            r.setPublished(p.isPublished());
        } else {
            r.setContactPhone(user.getPhone());
            r.setContactEmail(user.getEmail());
            r.setContactTelegram(user.getTelegram());
            r.setPublished(false);
        }

        r.setActive(user.isActive());
        r.setBanned(user.isBanned());
        r.setCreatedAt(user.getCreatedAt());
        r.setUpdatedAt(user.getUpdatedAt());
        return r;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private void validateProfileBeforePublish(PerformerProfile profile) {
        if ((profile.getMainPhotoUrl() == null || profile.getMainPhotoUrl().isBlank())
                && profile.getPhotoUrls() != null
                && !profile.getPhotoUrls().isEmpty()) {
            profile.setMainPhotoUrl(profile.getPhotoUrls().iterator().next());
        }

        if (profile.getMainPhotoUrl() == null || profile.getMainPhotoUrl().isBlank()) {
            throw new BadRequestException("Add photo before publishing");
        }
    }
}
