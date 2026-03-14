package com.casting.platform.dto.request.profile;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateCreatorProfileRequest {

    private String firstName;
    private String lastName;
    private String displayName;

    private String city;
    private String mainPhotoUrl;
    private String description;
    private String bio;

    private String activityType;
    private String experienceText;
    private String experienceLevel;
    private String projectFormatsJson;
    private String achievements;
    private String skillsJson;

    private Boolean published;

    private BigDecimal minRate;
    private String rateUnit;

    private String socialLinksJson;

    private String contactPhone;
    private String contactEmail;
    private String contactWhatsapp;
    private String contactTelegram;
    private String contactInstagram;

    private List<String> photoUrls;
    private List<String> videoUrls;
}
