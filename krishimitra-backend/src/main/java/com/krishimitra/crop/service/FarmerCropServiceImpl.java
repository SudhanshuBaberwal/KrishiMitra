package com.krishimitra.crop.service;

import com.krishimitra.crop.dto.request.CreateFarmerCropRequest;
import com.krishimitra.crop.dto.request.UpdateFarmerCropRequest;
import com.krishimitra.crop.dto.response.FarmerCropResponse;
import com.krishimitra.crop.entity.FarmerCrop;
import com.krishimitra.crop.exception.CropNotFoundException;
import com.krishimitra.crop.exception.DuplicateFarmerCropException;
import com.krishimitra.crop.exception.FarmerProfileNotFoundException;
import com.krishimitra.crop.repository.FarmerCropRepository;
import com.krishimitra.farmer.model.FarmerProfile;
import com.krishimitra.farmer.repository.FarmerRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class FarmerCropServiceImpl implements FarmerCropService {

    private final FarmerCropRepository cropRepository;
    private final FarmerRepository farmerRepository;

    public FarmerCropServiceImpl(
            FarmerCropRepository cropRepository,
            FarmerRepository farmerRepository
    ) {
        this.cropRepository = cropRepository;
        this.farmerRepository = farmerRepository;
    }

    @Override
    public FarmerCropResponse createCrop(
            CreateFarmerCropRequest request,
            String userId
    ) {

        FarmerProfile farmer = getFarmerProfile(userId);

        String cropCode = request.cropCode().trim().toUpperCase();
        String variety = request.variety().trim();

        boolean duplicate = cropRepository
                .existsByFarmerIdAndCropCodeAndVarietyAndSowingDateAndActiveTrue(
                        farmer.getId(),
                        cropCode,
                        variety,
                        request.sowingDate()
                );

        if (duplicate) {
            throw new DuplicateFarmerCropException(
                    "This crop variety with the same sowing date already exists"
            );
        }

        FarmerCrop crop = new FarmerCrop();

        crop.setFarmerId(farmer.getId());
        crop.setUserId(userId);

        crop.setCropCode(cropCode);
        crop.setCropName(request.cropName().trim());
        crop.setVariety(variety);

        crop.setSeason(request.season());
        crop.setSowingDate(request.sowingDate());
        crop.setGrowthStage(request.growthStage());

        crop.setArea(request.area());
        crop.setAreaUnit(request.areaUnit());

        crop.setExpectedHarvestDate(
                request.expectedHarvestDate()
        );

        crop.setActive(true);

        Instant now = Instant.now();
        crop.setCreatedAt(now);
        crop.setUpdatedAt(now);

        FarmerCrop savedCrop = cropRepository.save(crop);

        return mapToResponse(savedCrop);
    }

    @Override
    public List<FarmerCropResponse> getMyCrops(String userId) {

        FarmerProfile farmer = getFarmerProfile(userId);

        return cropRepository
                .findByFarmerIdOrderByCreatedAtDesc(farmer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public FarmerCropResponse getCropById(
            String cropId,
            String userId
    ) {

        FarmerCrop crop = getOwnedCrop(cropId, userId);

        return mapToResponse(crop);
    }

    @Override
    public FarmerCropResponse updateCrop(
            String cropId,
            UpdateFarmerCropRequest request,
            String userId
    ) {

        FarmerCrop crop = getOwnedCrop(cropId, userId);

        String cropCode = request.cropCode().trim().toUpperCase();
        String variety = request.variety().trim();

        FarmerProfile farmer = getFarmerProfile(userId);

        boolean duplicate = cropRepository
                .existsByFarmerIdAndCropCodeAndVarietyAndSowingDateAndActiveTrue(
                        farmer.getId(),
                        cropCode,
                        variety,
                        request.sowingDate()
                );

        if (duplicate && (
                !crop.getCropCode().equals(cropCode)
                        || !crop.getVariety().equalsIgnoreCase(variety)
                        || !crop.getSowingDate().equals(request.sowingDate())
        )) {
            throw new DuplicateFarmerCropException(
                    "Another active crop with the same variety and sowing date already exists"
            );
        }

        crop.setCropCode(cropCode);
        crop.setCropName(request.cropName().trim());
        crop.setVariety(variety);

        crop.setSeason(request.season());
        crop.setSowingDate(request.sowingDate());
        crop.setGrowthStage(request.growthStage());

        crop.setArea(request.area());
        crop.setAreaUnit(request.areaUnit());

        crop.setExpectedHarvestDate(
                request.expectedHarvestDate()
        );

        crop.setActive(request.active());

        crop.setUpdatedAt(Instant.now());

        FarmerCrop updatedCrop = cropRepository.save(crop);

        return mapToResponse(updatedCrop);
    }

    @Override
    public void deleteCrop(
            String cropId,
            String userId
    ) {

        FarmerCrop crop = getOwnedCrop(cropId, userId);

        // Soft delete: retain history for future advisory and analytics.
        crop.setActive(false);
        crop.setUpdatedAt(Instant.now());

        cropRepository.save(crop);
    }

    private FarmerProfile getFarmerProfile(String userId) {

        return farmerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new FarmerProfileNotFoundException(
                                "Farmer profile not found. Please create your profile first."
                        )
                );
    }

    private FarmerCrop getOwnedCrop(
            String cropId,
            String userId
    ) {

        return cropRepository.findByIdAndUserId(cropId, userId)
                .orElseThrow(() ->
                        new CropNotFoundException(
                                "Crop not found or you do not have permission to access it"
                        )
                );
    }

    private FarmerCropResponse mapToResponse(FarmerCrop crop) {

        return new FarmerCropResponse(
                crop.getId(),
                crop.getFarmerId(),
                crop.getCropCode(),
                crop.getCropName(),
                crop.getVariety(),
                crop.getSeason(),
                crop.getSowingDate(),
                crop.getGrowthStage(),
                crop.getArea(),
                crop.getAreaUnit(),
                crop.getExpectedHarvestDate(),
                crop.isActive(),
                crop.getCreatedAt(),
                crop.getUpdatedAt()
        );
    }
}