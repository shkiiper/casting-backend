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
public class UpdateCreatorProfileRequest {

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

    private String experienceLevel;
    @JsonAlias("projectFormatsJson")
    @Size(max = 5000)
    private String projectFormats;
    @JsonAlias("achievements")
    @Size(max = 5000)
    private String caseHighlights;
    @JsonAlias("skillsJson")
    @Size(max = 5000)
    private String skills;
    private String activityType;
    private List<String> activityTypes;

    private Boolean published;

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
    @JsonAlias("socialLinksJson")
    @Size(max = 255)
    private String websiteUrl;
    @JsonAlias("contactInstagram")
    @Size(max = 255)
    private String instagramUrl;

    private List<String> photoUrls;
    private List<String> videoUrls;

    @JsonSetter("projectFormats")
    public void setProjectFormats(JsonNode projectFormats) {
        this.projectFormats = jsonNodeToString(projectFormats);
    }

    public void setProjectFormats(String projectFormats) {
        this.projectFormats = projectFormats;
    }

    @JsonSetter("caseHighlights")
    public void setCaseHighlights(JsonNode caseHighlights) {
        this.caseHighlights = jsonNodeToString(caseHighlights);
    }

    public void setCaseHighlights(String caseHighlights) {
        this.caseHighlights = caseHighlights;
    }

    @JsonSetter("skills")
    public void setSkills(JsonNode skills) {
        this.skills = jsonNodeToString(skills);
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    private String jsonNodeToString(JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }

        return value.isTextual() ? value.asText() : value.toString();
    }
}
