package com.krishimitra.soil.model;

import com.krishimitra.soil.enums.SoilDataSource;
import com.krishimitra.soil.enums.SoilVerificationStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Document(collection = "soil_records")
public class SoilRecord {

    @Id
    private String id;

    @Indexed
    private String farmerId;

    @Indexed
    private String userId;

    private LocalDate sampleDate;

    private String sampleLocation;

    private SoilDataSource source;

    private SoilVerificationStatus verificationStatus;

    private String labName;

    private String reportReference;

    private SoilParameters parameters;

    private String notes;

    private boolean active = true;

    private Instant createdAt;

    private Instant updatedAt;
}