package com.casting.platform.service;

import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import com.casting.platform.repository.CastingPostRepository;
import com.casting.platform.repository.ContactViewRepository;
import com.casting.platform.repository.CustomerSubscriptionRepository;
import com.casting.platform.repository.EmailVerificationTokenRepository;
import com.casting.platform.repository.PasswordResetTokenRepository;
import com.casting.platform.repository.PaymentRepository;
import com.casting.platform.repository.PerformerProfileRepository;
import com.casting.platform.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PerformerProfileRepository performerProfileRepository;

    @Mock
    private ContactViewRepository contactViewRepository;

    @Mock
    private CustomerSubscriptionRepository customerSubscriptionRepository;

    @Mock
    private CastingPostRepository castingPostRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void getUsersIncludesNormalizedProfilePhotos() {
        User user = new User();
        user.setId(35L);
        user.setEmail("actor@example.com");
        user.setRole(UserRole.ACTOR);

        PerformerProfile profile = new PerformerProfile();
        profile.setId(12L);
        profile.setOwner(user);
        profile.setMainPhotoUrl("/uploads/images/main.jpg");
        profile.setPhotoUrls(new LinkedHashSet<>(List.of(
                "/uploads/images/main.jpg",
                "https://cdn.example.com/second.jpg"
        )));
        user.setPerformerProfile(profile);

        when(userRepository.findAll(org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<User>>any(), org.mockito.ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        ReflectionTestUtils.setField(adminUserService, "publicUrl", "https://onsetcasting.com");

        var response = adminUserService.getUsers(0, 20, null, null, null, null);

        assertEquals(1, response.getContent().size());
        assertEquals("https://onsetcasting.com/uploads/images/main.jpg", response.getContent().get(0).getMainPhotoUrl());
        assertEquals(
                List.of(
                        "https://onsetcasting.com/uploads/images/main.jpg",
                        "https://cdn.example.com/second.jpg"
                ),
                response.getContent().get(0).getPhotoUrls()
        );
    }

    @Test
    void deleteUserDeletesDependenciesBeforeProfileAndUser() {
        User user = new User();
        user.setId(6L);

        PerformerProfile profile = new PerformerProfile();
        profile.setId(10L);
        profile.setOwner(user);
        user.setPerformerProfile(profile);

        when(userRepository.findById(6L)).thenReturn(Optional.of(user));

        adminUserService.deleteUser(6L);

        var order = inOrder(
                userRepository,
                contactViewRepository,
                emailVerificationTokenRepository,
                passwordResetTokenRepository,
                paymentRepository,
                customerSubscriptionRepository,
                castingPostRepository,
                performerProfileRepository
        );

        order.verify(userRepository).findById(6L);
        order.verify(contactViewRepository).deleteByProfileId(10L);
        order.verify(emailVerificationTokenRepository).deleteByUserId(6L);
        order.verify(passwordResetTokenRepository).deleteByUserId(6L);
        order.verify(paymentRepository).deleteByCustomerId(6L);
        order.verify(customerSubscriptionRepository).deleteByCustomerId(6L);
        order.verify(castingPostRepository).deleteByCustomerId(6L);
        order.verify(contactViewRepository).deleteByCustomerId(6L);
        order.verify(performerProfileRepository).delete(profile);
        order.verify(userRepository).flush();
        order.verify(userRepository).delete(user);

        assertNull(user.getPerformerProfile());

        verifyNoMoreInteractions(
                userRepository,
                performerProfileRepository,
                contactViewRepository,
                customerSubscriptionRepository,
                castingPostRepository,
                paymentRepository,
                emailVerificationTokenRepository,
                passwordResetTokenRepository
        );
    }

    @Test
    void sendMissingPhotoReminderSendsEmailWhenProfileHasNoPhoto() {
        User user = new User();
        user.setId(7L);
        user.setEmail("user@example.com");

        PerformerProfile profile = new PerformerProfile();
        profile.setOwner(user);
        user.setPerformerProfile(profile);

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        adminUserService.sendMissingPhotoReminder(7L);

        verify(emailService).sendMissingPhotoReminderEmail("user@example.com");
    }

    @Test
    void sendMissingPhotoReminderFailsWhenProfileAlreadyHasPhoto() {
        User user = new User();
        user.setId(8L);
        user.setEmail("user@example.com");

        PerformerProfile profile = new PerformerProfile();
        profile.setOwner(user);
        profile.setMainPhotoUrl("https://cdn.example.com/photo.jpg");
        user.setPerformerProfile(profile);

        when(userRepository.findById(8L)).thenReturn(Optional.of(user));

        Assertions.assertThrows(
                com.casting.platform.exception.BadRequestException.class,
                () -> adminUserService.sendMissingPhotoReminder(8L)
        );
    }

    @Test
    void sendMissingPhotoReminderSendsEmailWhenUserHasNoProfile() {
        User user = new User();
        user.setId(14L);
        user.setEmail("kanyshaitn@gmail.com");

        when(userRepository.findById(14L)).thenReturn(Optional.of(user));

        adminUserService.sendMissingPhotoReminder(14L);

        verify(emailService).sendMissingPhotoReminderEmail("kanyshaitn@gmail.com");
    }

    @Test
    void sendMissingPhotoReminderFailsWhenUserAvatarAlreadyExists() {
        User user = new User();
        user.setId(15L);
        user.setEmail("user@example.com");
        user.setAvatarUrl("https://cdn.example.com/avatar.jpg");

        when(userRepository.findById(15L)).thenReturn(Optional.of(user));

        Assertions.assertThrows(
                com.casting.platform.exception.BadRequestException.class,
                () -> adminUserService.sendMissingPhotoReminder(15L)
        );
    }
}
