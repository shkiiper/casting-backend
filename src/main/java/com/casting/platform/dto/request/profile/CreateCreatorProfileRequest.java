package com.casting.platform.dto.request.profile;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateCreatorProfileRequest {

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

    // JSON string, e.g. ["commercial","music-video"]
    private String projectFormatsJson;
    private String achievements;

    // JSON string, e.g. ["directing","editing"]
    private String skillsJson;

    private BigDecimal minRate;
    private String rateUnit;

    /**
     * JSON string for socials/portfolio urls
     */
    private String socialLinksJson;

    private String contactPhone;
    private String contactEmail;
    private String contactWhatsapp;
    private String contactTelegram;
    private String contactInstagram;

    private List<String> photoUrls;
    private List<String> videoUrls;
}
