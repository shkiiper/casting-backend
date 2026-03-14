package com.casting.platform.dto.request.profile;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateActorProfileRequest {

    private String firstName;
    private String lastName;
    private String city;
    private String mainPhotoUrl;
    private String description;
    private String bio;
    private String experienceText;
    private Boolean published;

    private String gender; // MALE, FEMALE, ...
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

    private BigDecimal minRate;
    private String rateUnit;

    private String contactPhone;
    private String contactEmail;
    private String contactWhatsapp;
    private String contactTelegram;
    private String contactInstagram;

    private String introVideoUrl;
    private String monologueVideoUrl;
    private String selfTapeVideoUrl;

    private List<String> photoUrls;
    private List<String> videoUrls;
}
