
package com.krishimitra.farmer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "farmers")
public class FarmerProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String name;
    private String state;
    private String district;
    private String taluka;
    private String preferredLanguage;

    private String cropName;
    private String cropVariety;
    private String growthStage;

    private Double farmSize;
    private String irrigationType;

    private Instant createdAt;
    private Instant updatedAt;

    public FarmerProfile() {
    }
}
