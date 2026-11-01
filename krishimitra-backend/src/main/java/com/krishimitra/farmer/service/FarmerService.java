package com.krishimitra.farmer.service;

import com.krishimitra.farmer.dto.request.CreateFarmerRequest;
import com.krishimitra.farmer.dto.request.UpdateFarmerRequest;
import com.krishimitra.farmer.dto.response.FarmerResponse;
import com.krishimitra.farmer.exception.FarmerProfileAlreadyExistsException;
import com.krishimitra.farmer.model.FarmerProfile;
import com.krishimitra.farmer.repository.FarmerRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FarmerService {

    private final FarmerRepository farmerRepository;

    public FarmerService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    // Create farmer profile
    public FarmerResponse createFarmer(
            CreateFarmerRequest request,
            String userId
    ) {

        if (farmerRepository.existsByUserId(userId)) {
            throw new FarmerProfileAlreadyExistsException(
                    "Farmer profile already exists for this user"
            );
        }
        FarmerProfile farmer = new FarmerProfile();

        farmer.setUserId(userId);
        farmer.setName(request.name().trim());
        farmer.setState(request.state());
        farmer.setDistrict(request.district());
        farmer.setTaluka(request.taluka());
        farmer.setPreferredLanguage(request.preferredLanguage());
        farmer.setCropName(request.cropName());
        farmer.setCropVariety(request.cropVariety());
        farmer.setGrowthStage(request.growthStage());
        farmer.setFarmSize(request.farmSize());
        farmer.setIrrigationType(request.irrigationType());

        Instant now = Instant.now();
        farmer.setCreatedAt(now);
        farmer.setUpdatedAt(now);

        FarmerProfile savedFarmer = farmerRepository.save(farmer);

        return mapToResponse(savedFarmer);
    }

    // Get farmer profile using authenticated user's ID
    public FarmerResponse getFarmerByUserId(String userId) {

        FarmerProfile farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Farmer profile not found"));

        return mapToResponse(farmer);
    }

    // Update farmer profile using authenticated user's ID
    public FarmerResponse updateFarmer(
            UpdateFarmerRequest request,
            String userId
    ) {

        FarmerProfile farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Farmer profile not found"));

        farmer.setName(request.name().trim());
        farmer.setState(request.state());
        farmer.setDistrict(request.district());
        farmer.setTaluka(request.taluka());
        farmer.setPreferredLanguage(request.preferredLanguage());
        farmer.setCropName(request.cropName());
        farmer.setCropVariety(request.cropVariety());
        farmer.setGrowthStage(request.growthStage());
        farmer.setFarmSize(request.farmSize());
        farmer.setIrrigationType(request.irrigationType());

        farmer.setUpdatedAt(Instant.now());

        FarmerProfile updatedFarmer = farmerRepository.save(farmer);

        return mapToResponse(updatedFarmer);
    }

    // Convert MongoDB entity into response DTO
    private FarmerResponse mapToResponse(FarmerProfile farmer) {

        return new FarmerResponse(
                farmer.getId(),
                farmer.getUserId(),
                farmer.getName(),
                farmer.getState(),
                farmer.getDistrict(),
                farmer.getTaluka(),
                farmer.getPreferredLanguage(),
                farmer.getCropName(),
                farmer.getCropVariety(),
                farmer.getGrowthStage(),
                farmer.getFarmSize(),
                farmer.getIrrigationType(),
                farmer.getCreatedAt(),
                farmer.getUpdatedAt()
        );
    }
}