package com.casting.platform.dto.response.customer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionInfoResponse {
    private boolean active;
    private String planName;
    private int remainingContacts;
    private int totalLimit;
    private LocalDateTime expiresAt;
    private long daysRemaining;
}
