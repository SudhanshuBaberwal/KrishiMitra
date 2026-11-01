package com.krishimitra.dashboard.dto.response;

import com.krishimitra.crop.enums.AreaUnit;
import com.krishimitra.crop.enums.CropSeason;
import com.krishimitra.crop.enums.GrowthStage;

import java.time.LocalDate;

public record CropSummary(
        String id,
        String cropCode,
        String cropName,
        String variety,
        CropSeason season,
        LocalDate sowingDate,
        GrowthStage growthStage,
        Double area,
        AreaUnit areaUnit,
        LocalDate expectedHarvestDate
) {}