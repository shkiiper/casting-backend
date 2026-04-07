package com.casting.platform.dto.response.profile;

import com.casting.platform.entity.Gender;
import com.casting.platform.entity.PerformerType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class ProfileResponse {
    private Long id;
    private PerformerType type;

    private String firstName;
    private String lastName;
    private String displayName;
    private Boolean published;

    private String city;
    private String mainPhotoUrl;
    private List<String> photoUrls;
    private List<String> portfolioPhotoUrls;
    private List<String> videoUrls;

    private String description;
    private String bio;

    private BigDecimal minRate;
    private String rateUnit;
    private Boolean premiumActive;
    private LocalDateTime premiumUntil;
    private Boolean premiumPurchaseAvailable;
    private String premiumCheckoutEndpoint;
    private BigDecimal premiumPrice;
    private Integer premiumDurationDays;

    private String contactPhone;
    private String contactEmail;
    private String contactWhatsapp;
    private String contactTelegram;
    private String contactInstagram;
    private String websiteUrl;
    private String instagramUrl;

    // Actor
    private Gender gender;
    private Integer age;
    private String ethnicity;
    private Integer heightCm;
    private Integer weightKg;
    private String bodyType;
    private String hairColor;
    private String eyeColor;
    private Integer gameAgeFrom;
    private Integer gameAgeTo;
    private String skillsJson;
    private String introVideoUrl;
    private String monologueVideoUrl;
    private String selfTapeVideoUrl;

    // Creator
    private String activityType;
    private List<String> activityTypes;
    private String experienceText;
    private String experienceLevel;
    private String projectFormatsJson;
    private String achievements;
    private String socialLinksJson;
    private String projectFormats;
    private String caseHighlights;
    private String skills;

    // Location
    private String locationName;
    private String address;
    private BigDecimal rentPrice;
    private String rentPriceUnit;
    private Integer floor;
    private String locationType;
    private LocalTime availabilityFrom;
    private LocalTime availabilityTo;
    private String rentalTerms;
    private String extraConditions;
}
