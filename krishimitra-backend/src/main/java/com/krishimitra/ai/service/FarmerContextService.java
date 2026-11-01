package com.krishimitra.ai.service;

import com.krishimitra.crop.entity.FarmerCrop;
import com.krishimitra.crop.repository.FarmerCropRepository;
import com.krishimitra.farmer.model.FarmerProfile;
import com.krishimitra.farmer.repository.FarmerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FarmerContextService {

    private final FarmerRepository farmerRepository;
    private final FarmerCropRepository farmerCropRepository;

    public FarmerContextService(
            FarmerRepository farmerRepository,
            FarmerCropRepository farmerCropRepository
    ) {
        this.farmerRepository = farmerRepository;
        this.farmerCropRepository = farmerCropRepository;
    }

    public String buildContext(String userId) {

        FarmerProfile farmer = farmerRepository
                .findByUserId(userId)
                .orElse(null);

        if (farmer == null) {
            return "Farmer profile is not available. "
                    + "Do not assume the farmer's location or crops.";
        }

        List<FarmerCrop> crops = farmerCropRepository
                .findByFarmerIdAndActiveTrueOrderByCreatedAtDesc(
                        farmer.getId()
                );

        StringBuilder context = new StringBuilder();

        context.append("FARMER PROFILE:\n");
        context.append("Name: ").append(farmer.getName()).append("\n");
        context.append("State: ").append(farmer.getState()).append("\n");
        context.append("District: ").append(farmer.getDistrict()).append("\n");
        context.append("Taluka: ").append(farmer.getTaluka()).append("\n");
        context.append("Preferred language: ")
                .append(farmer.getPreferredLanguage())
                .append("\n");

        context.append("\nACTIVE CROPS:\n");

        if (crops.isEmpty()) {
            context.append("No active crops registered.\n");
        } else {
            for (FarmerCrop crop : crops) {

                context.append("Crop: ")
                        .append(crop.getCropName())
                        .append("\n");

                context.append("Variety: ")
                        .append(crop.getVariety())
                        .append("\n");

                context.append("Growth stage: ")
                        .append(crop.getGrowthStage())
                        .append("\n");

                context.append("Season: ")
                        .append(crop.getSeason())
                        .append("\n\n");
            }
        }

        return context.toString();
    }
}