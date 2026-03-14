package com.casting.platform.dto.response.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminUserResponse {
    private Long id;
    private String role;

    private String email;
    private String phone;
    private String city;
    private String description;
    private String telegram;

    private String firstName;
    private String lastName;
    private String displayName;

    private String contactPhone;
    private String contactEmail;
    private String contactTelegram;
    private String contactWhatsapp;

    private BigDecimal minRate;
    private String rateUnit;

    private boolean active;
    private boolean banned;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
