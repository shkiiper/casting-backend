package com.casting.platform.dto.request.profile;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateCreatorProfileRequest {

    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
    @Size(max = 255)
    private String displayName;

    @Size(max = 255)
    private String city;
    private String mainPhotoUrl;
    @Size(max = 5000)
    private String description;
    @Size(max = 5000)
    private String bio;

    private String activityType;
    @Size(max = 5000)
    private String experienceText;
    private String experienceLevel;

    // JSON string, e.g. ["commercial","music-video"]
    @Size(max = 5000)
    private String projectFormatsJson;
    @Size(max = 5000)
    private String achievements;

    // JSON string, e.g. ["directing","editing"]
    @Size(max = 5000)
    private String skillsJson;

    private BigDecimal minRate;
    private String rateUnit;

    /**
     * JSON string for socials/portfolio urls
     */
    @Size(max = 5000)
    private String socialLinksJson;

    @Size(max = 255)
    private String contactPhone;
    @Size(max = 255)
    private String contactEmail;
    @Size(max = 255)
    private String contactWhatsapp;
    @Size(max = 255)
    private String contactTelegram;
    @Size(max = 255)
    private String contactInstagram;

    private List<String> photoUrls;
    private List<String> videoUrls;
}
