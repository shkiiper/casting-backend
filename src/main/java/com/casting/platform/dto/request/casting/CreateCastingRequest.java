package com.casting.platform.dto.request.casting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCastingRequest {

    @NotBlank
    @Size(min = 5, max = 200)
    private String title;

    @NotBlank
    @Size(min = 20, max = 5000)
    private String description;

    private String city;
    private String projectType;
}
