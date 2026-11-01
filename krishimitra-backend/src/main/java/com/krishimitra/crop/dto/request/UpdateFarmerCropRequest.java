package com.krishimitra.crop.dto.request;

import com.krishimitra.crop.enums.AreaUnit;
import com.krishimitra.crop.enums.CropSeason;
import com.krishimitra.crop.enums.GrowthStage;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateFarmerCropRequest(

        @NotBlank(message = "Crop code is required")
        @Size(max = 50)
        String cropCode,

        @NotBlank(message = "Crop name is required")
        @Size(max = 100)
        String cropName,

        @NotBlank(message = "Crop variety is required")
        @Size(max = 100)
        String variety,

        @NotNull(message = "Crop season is required")
        CropSeason season,

        @NotNull(message = "Sowing date is required")
        @PastOrPresent(message = "Sowing date cannot be in the future")
        LocalDate sowingDate,

        @NotNull(message = "Growth stage is required")
        GrowthStage growthStage,

        @NotNull(message = "Area is required")
        @Positive(message = "Area must be greater than zero")
        @DecimalMax(value = "100000")
        Double area,

        @NotNull(message = "Area unit is required")
        AreaUnit areaUnit,

        @Future(message = "Expected harvest date must be in the future")
        LocalDate expectedHarvestDate,

        @NotNull(message = "Active status is required")
        Boolean active

) {}