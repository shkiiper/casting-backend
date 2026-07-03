package com.casting.platform.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.casting.platform.entity.UserRole;

@Data
public class ResendVerificationRequest {
    @Email
    @NotBlank
    private String email;

    private UserRole role;
}
