package com.krishimitra.farmer.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFarmerRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
        String name,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "District is required")
        String district,

        @NotBlank(message = "Taluka is required")
        String taluka,

        @NotBlank(message = "Preferred language is required")
        String preferredLanguage,

        @NotBlank(message = "Crop name is required")
        String cropName,

        String cropVariety,

        @NotBlank(message = "Growth stage is required")
        String growthStage,

        @DecimalMin(value = "0.01", message = "Farm size must be greater than zero")
        Double farmSize,

        String irrigationType
) {}