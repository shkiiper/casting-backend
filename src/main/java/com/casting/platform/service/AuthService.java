package com.casting.platform.service;

import com.casting.platform.dto.request.auth.*;
import com.casting.platform.dto.response.auth.AuthResponse;
import com.casting.platform.entity.EmailVerificationToken;
import com.casting.platform.entity.PasswordResetToken;
import com.casting.platform.entity.User;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.casting.platform.entity.PerformerProfile;
import com.casting.platform.entity.PerformerType;
import com.casting.platform.repository.PerformerProfileRepository;

import java.time.LocalDateTime;
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
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PerformerProfileRepository profileRepository;

    @Value("${app.tokens.emailVerificationTtlMinutes:10}")
    private long emailVerificationTtlMinutes;

    @Value("${app.tokens.passwordResetTtlMinutes:30}")
    private long passwordResetTtlMinutes;

    private static final SecureRandom random = new SecureRandom();

    /* =========================================================
       REGISTER
       ========================================================= */

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()
                && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEmailVerified(false);
        user.setActive(true);

        user = userRepository.save(user);


        createAndSendEmailCode(user);

        return new AuthResponse(null, user.getRole().name());
    }

    /* =========================================================
       LOGIN
       ========================================================= */

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isEmailVerified()) {
            throw new ForbiddenException("Email is not verified");
        }

        if (!user.isActive()) {
            throw new ForbiddenException("User disabled");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String token = jwtTokenProvider.generateToken(
                principal.getUsername(),
                principal.getRole()
        );

        return new AuthResponse(token, principal.getRole().name());
    }

    /* =========================================================
       VERIFY EMAIL CODE
       ========================================================= */

    public void verifyEmailCode(String email, String code) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

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

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.isEmailVerified()) {
            return;
        }

        emailVerificationTokenRepository.deleteByUserId(user.getId());

        createAndSendEmailCode(user);
    }

    /* =========================================================
       FORGOT PASSWORD (оставил по ссылке)
       ========================================================= */

    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
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

        String link = "http://localhost:8080/api/auth/reset-password?token=" + prt.getToken();

        emailService.send(
                user.getEmail(),
                "Сброс пароля",
                "Для сброса пароля перейдите по ссылке:\n" + link
        );
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
}