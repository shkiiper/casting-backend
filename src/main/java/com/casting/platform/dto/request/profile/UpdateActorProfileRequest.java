package com.casting.platform.dto.request.profile;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateActorProfileRequest {

    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
    @Size(max = 255)
    private String city;
    private String mainPhotoUrl;
    @Size(max = 5000)
    private String description;
    @Size(max = 5000)
    private String bio;
    @Size(max = 5000)
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
    @JsonAlias("playingAgeMin")
    private Integer gameAgeFrom;
    @JsonAlias("playingAgeMax")
    private Integer gameAgeTo;
    @Size(max = 5000)
    private String skillsJson;

    private BigDecimal minRate;
    private String rateUnit;

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

    private String introVideoUrl;
    private String monologueVideoUrl;
    private String selfTapeVideoUrl;

    private List<String> photoUrls;
    private List<String> videoUrls;

    @JsonSetter("skillsJson")
    public void setSkillsJson(JsonNode skillsJson) {
        if (skillsJson == null || skillsJson.isNull()) {
            this.skillsJson = null;
            return;
        }

        this.skillsJson = skillsJson.isTextual()
                ? skillsJson.asText()
                : skillsJson.toString();
    }
}
