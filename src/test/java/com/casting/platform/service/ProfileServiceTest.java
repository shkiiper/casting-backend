package com.casting.platform.service;

import com.casting.platform.dto.request.profile.UpdateActorProfileRequest;
import com.casting.platform.dto.response.profile.ProfileResponse;
import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.PerformerType;
import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import com.casting.platform.repository.CustomerSubscriptionPlanRepository;
import com.casting.platform.repository.PerformerProfileRepository;
import com.casting.platform.repository.UserRepository;
import com.casting.platform.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PerformerProfileRepository profileRepository;

    @Mock
    private CustomerSubscriptionPlanRepository planRepository;

    @InjectMocks
    private ProfileService profileService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateActorProfileClearsMainPhotoWhenGalleryBecomesEmpty() {
        User user = new User();
        user.setId(3L);
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");
        user.setRole(UserRole.ACTOR);
        user.setActive(true);

        PerformerProfile profile = new PerformerProfile();
        profile.setId(9L);
        profile.setOwner(user);
        profile.setType(PerformerType.ACTOR);
        profile.setPublished(true);
        profile.setMainPhotoUrl("/uploads/images/old-main.jpg");
        profile.setPhotoUrls(new java.util.LinkedHashSet<>(List.of("/uploads/images/old-main.jpg")));

        user.setPerformerProfile(profile);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole(), true, false),
                        null
                )
        );

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(profileRepository.findByOwnerId(3L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(PerformerProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(planRepository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of());

        ReflectionTestUtils.setField(profileService, "publicUrl", "https://onsetcasting.com");

        UpdateActorProfileRequest request = new UpdateActorProfileRequest();
        request.setPhotoUrls(List.of());

        ProfileResponse response = profileService.updateActorProfile(request);

        assertTrue(profile.getPhotoUrls().isEmpty());
        assertNull(profile.getMainPhotoUrl());
        assertTrue(!profile.isPublished());
        assertNull(response.getMainPhotoUrl());
        assertTrue(response.getPhotoUrls().isEmpty());
        assertTrue(!response.getPublished());
    }
}
