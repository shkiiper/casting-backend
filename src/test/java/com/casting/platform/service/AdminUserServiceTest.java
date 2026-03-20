package com.casting.platform.service;

import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.User;
import com.casting.platform.repository.CastingPostRepository;
import com.casting.platform.repository.ContactViewRepository;
import com.casting.platform.repository.CustomerSubscriptionRepository;
import com.casting.platform.repository.EmailVerificationTokenRepository;
import com.casting.platform.repository.PasswordResetTokenRepository;
import com.casting.platform.repository.PaymentRepository;
import com.casting.platform.repository.PerformerProfileRepository;
import com.casting.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.inOrder;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @InjectMocks
    private AdminUserService adminUserService;

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
}
