package com.krishimitra.crop.dto.response;

import com.krishimitra.crop.enums.AreaUnit;
import com.krishimitra.crop.enums.CropSeason;
import com.krishimitra.crop.enums.GrowthStage;

import java.time.Instant;
import java.time.LocalDate;

public record FarmerCropResponse(

        String id,
        String farmerId,
        String cropCode,
        String cropName,
        String variety,
        CropSeason season,
        LocalDate sowingDate,
        GrowthStage growthStage,
        Double area,
        AreaUnit areaUnit,
        LocalDate expectedHarvestDate,
        boolean active,
        Instant createdAt,
        Instant updatedAt

) {}