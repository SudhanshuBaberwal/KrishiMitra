package com.krishimitra.dashboard.dto.response;

import java.time.Instant;
import java.util.List;

public record DashboardOverviewResponse(
        FarmerSummary farmer,
        List<CropSummary> crops,
        SoilSummary latestSoilRecord,
        WeatherSummary weather,
        List<AdvisorySummary> advisories,
        List<String> warnings,
        Instant generatedAt
) {}