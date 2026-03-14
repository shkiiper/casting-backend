package com.casting.platform.dto.response.customer;

import lombok.Data;

@Data
public class SubscriptionInfoResponse {
    private boolean active;
    private String planName;
    private int remainingContacts;
    private int totalLimit;
}
