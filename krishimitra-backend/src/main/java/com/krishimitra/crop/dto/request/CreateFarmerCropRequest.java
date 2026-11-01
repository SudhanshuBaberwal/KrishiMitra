package com.krishimitra.crop.dto.request;

import com.krishimitra.crop.enums.AreaUnit;
import com.krishimitra.crop.enums.CropSeason;
import com.krishimitra.crop.enums.GrowthStage;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateFarmerCropRequest(

        @NotBlank(message = "Crop code is required")
        @Size(max = 50, message = "Crop code cannot exceed 50 characters")
        String cropCode,

        @NotBlank(message = "Crop name is required")
        @Size(max = 100, message = "Crop name cannot exceed 100 characters")
        String cropName,

        @NotBlank(message = "Crop variety is required")
        @Size(max = 100, message = "Variety cannot exceed 100 characters")
        String variety,

        @NotNull(message = "Crop season is required")
        CropSeason season,

        @NotNull(message = "Sowing date is required")
        @PastOrPresent(message = "Sowing date cannot be in the future")
        LocalDate sowingDate,

        @NotNull(message = "Growth stage is required")
        GrowthStage growthStage,

        @NotNull(message = "Cultivated area is required")
        @Positive(message = "Area must be greater than zero")
        @DecimalMax(value = "100000", message = "Area is too large")
        Double area,

        @NotNull(message = "Area unit is required")
        AreaUnit areaUnit,

        @Future(message = "Expected harvest date must be in the future")
        LocalDate expectedHarvestDate

) {}