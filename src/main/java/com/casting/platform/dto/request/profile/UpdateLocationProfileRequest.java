package com.casting.platform.dto.request.profile;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class UpdateLocationProfileRequest {

    @Size(max = 255)
    private String locationName;
    @Size(max = 255)
    private String address;
    @Size(max = 255)
    private String city;
    private String mainPhotoUrl;
    @Size(max = 5000)
    private String description;
    private Boolean published;

    private BigDecimal rentPrice;
    private String rentPriceUnit;
    private Integer floor;
    private String locationType;
    private LocalTime availabilityFrom;
    private LocalTime availabilityTo;
    @Size(max = 5000)
    private String rentalTerms;
    @Size(max = 5000)
    private String extraConditions;

    @Size(max = 255)
    private String contactPhone;
    @Size(max = 255)
    private String contactEmail;
    @Size(max = 255)
    private String contactWhatsapp;
    @Size(max = 255)
    private String contactTelegram;

    private List<String> photoUrls;
    private List<String> videoUrls;
}
