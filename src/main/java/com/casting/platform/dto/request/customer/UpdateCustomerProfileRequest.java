package com.casting.platform.dto.request.customer;

import lombok.Data;

@Data
public class UpdateCustomerProfileRequest {

    private String displayName;
    private String city;
    private String description;

    private String contactPhone;
    private String contactEmail;
    private String contactTelegram;

    // ✅ АВАТАР ЗАКАЗЧИКА
    private String mainPhotoUrl;
}
