package com.casting.platform.controller;

import com.casting.platform.dto.request.auth.*;
import com.casting.platform.dto.response.auth.AuthResponse;
import com.casting.platform.dto.response.common.MessageResponse;
import com.casting.platform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /* ================= REGISTER ================= */

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    /* ================= LOGIN ================= */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    /* ================= VERIFY EMAIL ================= */

    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {

        authService.verifyEmailCode(
                request.getEmail(),
                request.getCode()
        );

        return ResponseEntity.ok(
                new MessageResponse("Email verified")
        );
    }

    /* ================= RESEND CODE ================= */

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resend(
            @Valid @RequestBody ResendVerificationRequest request
    ) {

        authService.resendVerification(request);

        return ResponseEntity.ok(
                new MessageResponse("Code sent")
        );
    }

    /* ================= FORGOT PASSWORD ================= */

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(
                new MessageResponse("Password reset email sent")
        );
    }

    /* ================= RESET PASSWORD ================= */

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                new MessageResponse("Password updated")
        );
    }
}