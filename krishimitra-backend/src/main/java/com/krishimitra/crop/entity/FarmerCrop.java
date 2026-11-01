package com.krishimitra.crop.entity;

import com.krishimitra.crop.enums.AreaUnit;
import com.krishimitra.crop.enums.CropSeason;
import com.krishimitra.crop.enums.GrowthStage;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "farmer_crops")
@Data
public class FarmerCrop {

    @Id
    private String id;

    @Indexed
    private String farmerId;

    @Indexed
    private String userId;

    private String cropCode;

    private String cropName;

    private String variety;

    private CropSeason season;

    private LocalDate sowingDate;

    private GrowthStage growthStage;

    private Double area;

    private AreaUnit areaUnit;

    private LocalDate expectedHarvestDate;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public FarmerCrop() {
    }


}