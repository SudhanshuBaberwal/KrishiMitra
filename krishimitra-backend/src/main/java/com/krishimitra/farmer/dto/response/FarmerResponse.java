package com.krishimitra.farmer.dto.response;

import java.time.Instant;

public record FarmerResponse(
        String id,
        String userId,
        String name,
        String state,
        String district,
        String taluka,
        String preferredLanguage,
        String cropName,
        String cropVariety,
        String growthStage,
        Double farmSize,
        String irrigationType,
        Instant createdAt,
        Instant updatedAt
) {}