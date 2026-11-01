package com.krishimitra.soil.dto.request;

import com.krishimitra.soil.enums.SoilDataSource;
import com.krishimitra.soil.model.SoilParameters;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateSoilRecordRequest(

        @NotNull(message = "Sample date is required")
        @PastOrPresent(message = "Sample date cannot be in the future")
        LocalDate sampleDate,

        @Size(max = 150, message = "Sample location cannot exceed 150 characters")
        String sampleLocation,

        @NotNull(message = "Soil data source is required")
        SoilDataSource source,

        @Size(max = 150, message = "Lab name cannot exceed 150 characters")
        String labName,

        @Size(max = 100, message = "Report reference cannot exceed 100 characters")
        String reportReference,

        @NotNull(message = "Soil parameters are required")
        @Valid
        SoilParameterRequest parameters,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes

) {

    public record SoilParameterRequest(

            @DecimalMin(value = "0.0", message = "pH cannot be below 0")
            @DecimalMax(value = "14.0", message = "pH cannot exceed 14")
            Double pH,

            @DecimalMin(value = "0.0", message = "EC cannot be negative")
            @DecimalMax(value = "100.0", message = "EC value is out of range")
            Double electricalConductivity,

            @DecimalMin(value = "0.0", message = "Organic carbon cannot be negative")
            @DecimalMax(value = "100.0", message = "Organic carbon value is out of range")
            Double organicCarbon,

            @DecimalMin(value = "0.0", message = "Nitrogen cannot be negative")
            Double nitrogen,

            @DecimalMin(value = "0.0", message = "Phosphorus cannot be negative")
            Double phosphorus,

            @DecimalMin(value = "0.0", message = "Potassium cannot be negative")
            Double potassium,

            @DecimalMin(value = "0.0", message = "Sulphur cannot be negative")
            Double sulphur,

            @DecimalMin(value = "0.0", message = "Zinc cannot be negative")
            Double zinc,

            @DecimalMin(value = "0.0", message = "Iron cannot be negative")
            Double iron,

            @DecimalMin(value = "0.0", message = "Manganese cannot be negative")
            Double manganese,

            @DecimalMin(value = "0.0", message = "Copper cannot be negative")
            Double copper,

            @DecimalMin(value = "0.0", message = "Boron cannot be negative")
            Double boron
    ) {}
}