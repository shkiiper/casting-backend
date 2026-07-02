package com.casting.platform.dto.request.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateLocationProfileRequest {
    @NotBlank
    private String locationName;

    private String address;

    @NotBlank
    private String city;

    private String mainPhotoUrl;

    private String description;

    private BigDecimal rentPrice;

    private String rentPriceUnit; // HOUR, DAY, PROJECT

    private Integer floor;
    private String locationType;
    private LocalTime availabilityFrom;
    private LocalTime availabilityTo;
    private String rentalTerms;
    private String extraConditions;

    private String contactPhone;

    private String contactEmail;
    private String contactWhatsapp;
    private String contactTelegram;

    private List<String> photoUrls;
    private List<String> videoUrls;
}
