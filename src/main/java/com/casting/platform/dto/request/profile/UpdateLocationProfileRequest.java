package com.casting.platform.dto.request.profile;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class UpdateLocationProfileRequest {

    private String locationName;
    private String address;
    private String city;
    private String mainPhotoUrl;
    private String description;
    private Boolean published;

    private BigDecimal rentPrice;
    private String rentPriceUnit;
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
