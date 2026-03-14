package com.casting.platform.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyEmailRequest {

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 6)
    private String code;
}
