package com.krishimitra.dashboard.dto.response;

import com.krishimitra.soil.enums.SoilDataSource;
import com.krishimitra.soil.enums.SoilVerificationStatus;
import com.krishimitra.soil.model.SoilParameters;

import java.time.LocalDate;

public record SoilSummary(
        String id,
        LocalDate sampleDate,
        String sampleLocation,
        SoilDataSource source,
        SoilVerificationStatus verificationStatus,
        String labName,
        SoilParameters parameters,
        String notes
) {}