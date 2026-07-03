package com.casting.platform.dto.request.auth;

import com.casting.platform.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SwitchRoleRequest {
    @NotNull
    private UserRole role;
}
