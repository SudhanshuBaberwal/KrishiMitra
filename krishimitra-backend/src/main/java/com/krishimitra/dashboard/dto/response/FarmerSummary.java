package com.krishimitra.dashboard.dto.response;

public record FarmerSummary(
        String id,
        String name,
        String state,
        String district,
        String taluka,
        String preferredLanguage,
        Double farmSize,
        String irrigationType
) {}