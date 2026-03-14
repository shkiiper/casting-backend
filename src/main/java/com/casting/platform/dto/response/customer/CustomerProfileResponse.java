package com.casting.platform.dto.response.customer;

import lombok.Data;

@Data
public class CustomerProfileResponse {

    private Long id;
    private String displayName;
    private String city;
    private String description;
    private String mainPhotoUrl;

    private String contactEmail;
    private String contactPhone;
    private String contactTelegram;

    private Boolean published;
}