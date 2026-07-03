package com.casting.platform.service;

import com.casting.platform.dto.request.auth.*;
import com.casting.platform.dto.response.auth.AuthResponse;
import com.casting.platform.entity.EmailVerificationToken;
import com.casting.platform.entity.PasswordResetToken;
import com.casting.platform.entity.User;
import com.casting.platform.entity.UserRole;
import com.casting.platform.exception.BadRequestException;
import com.casting.platform.exception.ForbiddenException;
import com.casting.platform.exception.NotFoundException;
import com.casting.platform.repository.EmailVerificationTokenRepository;
import com.casting.platform.repository.PasswordResetTokenRepository;
import com.casting.platform.repository.UserRepository;
import com.casting.platform.security.JwtTokenProvider;
import com.casting.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${app.tokens.emailVerificationTtlMinutes:10}")
    private long emailVerificationTtlMinutes;

    @Value("${app.tokens.passwordResetTtlMinutes:30}")
    private long passwordResetTtlMinutes;

    @Value("${app.passwordResetUrl:https://onsetcasting.com/reset-password}")
    private String passwordResetUrl;

    private static final SecureRandom random = new SecureRandom();

    /* =========================================================
       REGISTER
       ========================================================= */

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmailAndRole(request.getEmail(), request.getRole())) {
            throw new BadRequestException("Account for this email and role already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEmailVerified(false);
        user.setActive(true);

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Account for this email or phone already exists");
        }


        createAndSendEmailCode(user);

        return new AuthResponse(null, user.getRole().name(), getAvailableRoles(user.getEmail()));
    }

    /* =========================================================
       LOGIN
       ========================================================= */

    public AuthResponse login(LoginRequest request) {

        User user = resolveLoginUser(request);

        if (!user.isEmailVerified()) {
            throw new ForbiddenException("Email is not verified");
        }

        if (!user.isActive()) {
            throw new ForbiddenException("User disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(token, user.getRole().name(), getAvailableRoles(user.getEmail()));
    }

    /* =========================================================
       VERIFY EMAIL CODE
       ========================================================= */

    public void verifyEmailCode(String email, String code, UserRole role) {

        User user = resolveEmailRoleUser(email, role);

        EmailVerificationToken token =
                emailVerificationTokenRepository
                        .findTopByUserOrderByExpiresAtDesc(user)
                        .orElseThrow(() -> new BadRequestException("Code not found"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Code expired");
        }

        if (!token.getCode().equals(code)) {
            throw new BadRequestException("Invalid code");
        }

        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationTokenRepository.deleteByUserId(user.getId());
    }

    /* =========================================================
       RESEND CODE
       ========================================================= */

    public void resendVerification(ResendVerificationRequest request) {

        User user = resolveEmailRoleUser(request.getEmail(), request.getRole());

        if (user.isEmailVerified()) {
            return;
        }

        emailVerificationTokenRepository.deleteByUserId(user.getId());
        emailVerificationTokenRepository.flush();

        createAndSendEmailCode(user);
    }

    /* =========================================================
       FORGOT PASSWORD (оставил по ссылке)
       ========================================================= */

    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmailOrderByIdAsc(request.getEmail())
                .stream()
                .filter(User::isEmailVerified)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isEmailVerified()) {
            throw new ForbiddenException("Email is not verified");
        }

        passwordResetTokenRepository.deleteByUserId(user.getId());

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(UUID.randomUUID().toString());
        prt.setUser(user);
        prt.setExpiresAt(LocalDateTime.now().plusMinutes(passwordResetTtlMinutes));

        passwordResetTokenRepository.save(prt);

        String link = passwordResetUrl + "?token=" + prt.getToken();

        emailService.sendPasswordResetEmail(user.getEmail(), link);
    }

    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken prt = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token expired");
        }

        User user = prt.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        prt.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(prt);
    }

    public List<UserRole> getAvailableRolesForCurrentUser() {
        return getAvailableRoles(getCurrentPrincipal().getUsername());
    }

    public AuthResponse switchRole(SwitchRoleRequest request) {
        UserPrincipal principal = getCurrentPrincipal();

        User target = userRepository.findByEmailAndRole(principal.getUsername(), request.getRole())
                .orElseThrow(() -> new NotFoundException("Account for this role not found"));

        if (!target.isEmailVerified()) {
            throw new ForbiddenException("Email is not verified for this role");
        }

        if (!target.isActive() || target.isBanned()) {
            throw new ForbiddenException("User disabled");
        }

        String token = jwtTokenProvider.generateToken(target.getEmail(), target.getRole());
        return new AuthResponse(token, target.getRole().name(), getAvailableRoles(target.getEmail()));
    }

    /* =========================================================
       INTERNAL
       ========================================================= */

    private void createAndSendEmailCode(User user) {

        String code = generateCode();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setCode(code);
        token.setToken(UUID.randomUUID().toString());   // ⭐ ВАЖНО
        token.setExpiresAt(
                LocalDateTime.now().plusMinutes(emailVerificationTtlMinutes)
        );

        emailVerificationTokenRepository.save(token);

        emailService.sendVerificationCode(user.getEmail(), code);
    }

    private String generateCode() {
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    private User resolveLoginUser(LoginRequest request) {
        if (request.getRole() != null) {
            return userRepository.findByEmailAndRole(request.getEmail(), request.getRole())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        }

        return userRepository.findByEmailOrderByIdAsc(request.getEmail())
                .stream()
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
    }

    private User resolveEmailRoleUser(String email, UserRole role) {
        if (role != null) {
            return userRepository.findByEmailAndRole(email, role)
                    .orElseThrow(() -> new NotFoundException("User not found"));
        }

        return userRepository.findByEmailOrderByIdDesc(email)
                .stream()
                .filter(user -> !user.isEmailVerified())
                .findFirst()
                .orElseGet(() -> userRepository.findByEmailOrderByIdDesc(email)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("User not found")));
    }

    private List<UserRole> getAvailableRoles(String email) {
        return userRepository.findByEmailOrderByIdAsc(email)
                .stream()
                .filter(user -> user.isActive() && !user.isBanned())
                .map(User::getRole)
                .distinct()
                .toList();
    }

    private UserPrincipal getCurrentPrincipal() {
        Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ForbiddenException("Unauthenticated");
        }

        return principal;
    }
}
