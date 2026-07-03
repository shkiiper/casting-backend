package com.casting.platform.dto.response.auth;

import com.casting.platform.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private List<UserRole> availableRoles;

    public AuthResponse(String token, String role) {
        this(token, role, List.of());
    }
}
