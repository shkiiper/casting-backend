package com.casting.platform.dto.response.customer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewedContactResponse {
    private Long profileId;
    private String profileType;   // ACTOR/CREATOR/LOCATION
    private String displayName;   // имя или название (минимально)
    private String city;
    private String mainPhotoUrl;
    private LocalDateTime viewedAt;
}
