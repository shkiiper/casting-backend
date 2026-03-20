package com.casting.platform.dto.request.profile;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateActorProfileRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 100)
    private String lastName;

    @Size(max = 255)
    private String city;

    @NotBlank
    private String mainPhotoUrl;

    @Size(max = 5000)
    private String description;

    @Size(max = 5000)
    private String bio;

    private String experienceText;

    @NotNull
    private String gender;

    @Min(14)
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

    // JSON string for chips/multiselect, e.g. ["stunts","singing"]
    private String skillsJson;

    private BigDecimal minRate;

    @NotNull
    private String rateUnit; // HOUR, DAY, PROJECT

    @NotBlank
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
