package com.casting.platform.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalContactViews;
    private long totalPayments;
    private long totalCastingPosts;
}
