package com.casting.platform.dto.response.casting;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CastingResponse {

    private Long id;
    private Long customerId;
    private String title;
    private String description;
    private String city;
    private String projectType;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    private boolean active;
}
