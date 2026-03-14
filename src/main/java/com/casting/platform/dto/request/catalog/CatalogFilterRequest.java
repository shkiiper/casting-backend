package com.casting.platform.dto.request.catalog;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CatalogFilterRequest {

    // Общие фильтры
    private String city;

    // Актёры
    @Min(0)
    private Integer minAge;

    @Max(120)
    private Integer maxAge;

    // "MALE", "FEMALE", "OTHER"
    private String gender;

    // Этническая принадлежность (например "KYRGYZ", "RUSSIAN", "UZBEK", "OTHER")
    private String ethnicity;

    // Ставка (для актёров)
    private Double minRate;
    private Double maxRate;
    private String rateUnit; // "PER_HOUR", "PER_DAY", "PER_PROJECT"

    // Креаторы (например: "PHOTOGRAPHER", "DIRECTOR", ...)
    private String activityType;

    // Локации
    private Double minRentPrice;
    private Double maxRentPrice;

    // Пагинация
    @Min(0)
    private Integer page = 0;

    @Min(1)
    @Max(100)
    private Integer size = 20;
}
