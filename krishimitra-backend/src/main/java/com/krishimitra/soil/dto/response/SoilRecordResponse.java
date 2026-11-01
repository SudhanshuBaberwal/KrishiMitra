package com.krishimitra.soil.dto.response;

import com.krishimitra.soil.enums.SoilDataSource;
import com.krishimitra.soil.enums.SoilVerificationStatus;
import com.krishimitra.soil.model.SoilParameters;

import java.time.Instant;
import java.time.LocalDate;

public record SoilRecordResponse(
        String id,
        String farmerId,
        LocalDate sampleDate,
        String sampleLocation,
        SoilDataSource source,
        SoilVerificationStatus verificationStatus,
        String labName,
        String reportReference,
        SoilParameters parameters,
        String notes,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}