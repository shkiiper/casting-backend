package com.casting.platform.service;

import com.casting.platform.dto.request.profile.UpdateActorProfileRequest;
import com.casting.platform.dto.request.profile.UpdateCreatorProfileRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void updateCreatorProfileMapsFrontendFieldNamesToStoredFields() {
        User user = new User();
        user.setId(4L);
        user.setEmail("creator@example.com");
        user.setPasswordHash("hash");
        user.setRole(UserRole.CREATOR);
        user.setActive(true);

        PerformerProfile profile = new PerformerProfile();
        profile.setId(10L);
        profile.setOwner(user);
        profile.setType(PerformerType.CREATOR);
        profile.setPhotoUrls(new java.util.LinkedHashSet<>(List.of("/uploads/images/main.jpg")));
        profile.setMainPhotoUrl("/uploads/images/main.jpg");

        user.setPerformerProfile(profile);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole(), true, false),
                        null
                )
        );

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(profileRepository.findByOwnerId(4L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(PerformerProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(planRepository.findByActiveTrueOrderByIdAsc()).thenReturn(List.of());

        ReflectionTestUtils.setField(profileService, "publicUrl", "https://onsetcasting.com");

        UpdateCreatorProfileRequest request = new UpdateCreatorProfileRequest();
        request.setPublished(true);
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setCity("Bishkek");
        request.setDescription("Creator description");
        request.setBio("Creator bio");
        request.setExperienceLevel("senior");
        request.setProjectFormats("[\"reels\",\"ads\"]");
        request.setCaseHighlights("Top campaigns");
        request.setSkills("[\"direction\",\"editing\"]");
        request.setActivityType("director");
        request.setMinRate(new java.math.BigDecimal("1500"));
        request.setRateUnit("PROJECT");
        request.setContactPhone("+996700000000");
        request.setContactEmail("hello@example.com");
        request.setContactWhatsapp("+996700000001");
        request.setContactTelegram("@creator");
        request.setWebsiteUrl("https://site.example");
        request.setInstagramUrl("https://instagram.com/creator");
        request.setPhotoUrls(List.of("/uploads/images/main.jpg", "/uploads/images/second.jpg"));
        request.setVideoUrls(List.of("https://video.example/showreel"));

        ProfileResponse response = profileService.updateCreatorProfile(request);

        assertTrue(profile.isPublished());
        assertEquals("[\"reels\",\"ads\"]", profile.getProjectFormatsJson());
        assertEquals("Top campaigns", profile.getAchievements());
        assertEquals("[\"direction\",\"editing\"]", profile.getSkillsJson());
        assertEquals("https://site.example", profile.getSocialLinksJson());
        assertEquals("https://instagram.com/creator", profile.getContactInstagram());

        assertEquals("[\"reels\",\"ads\"]", response.getProjectFormats());
        assertEquals("Top campaigns", response.getCaseHighlights());
        assertEquals("[\"direction\",\"editing\"]", response.getSkills());
        assertEquals("https://site.example", response.getWebsiteUrl());
        assertEquals("https://instagram.com/creator", response.getInstagramUrl());
    }
}
