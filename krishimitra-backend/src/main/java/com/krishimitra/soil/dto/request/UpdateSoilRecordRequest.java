package com.krishimitra.soil.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record UpdateSoilRecordRequest(

        @Size(max = 150, message = "Sample location cannot exceed 150 characters")
        String sampleLocation,

        @Size(max = 150, message = "Lab name cannot exceed 150 characters")
        String labName,

        @Size(max = 100, message = "Report reference cannot exceed 100 characters")
        String reportReference,

        @Valid
        CreateSoilRecordRequest.SoilParameterRequest parameters,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes

) {}