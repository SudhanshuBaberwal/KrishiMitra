package com.krishimitra.crop.service;

import com.krishimitra.crop.dto.request.CreateFarmerCropRequest;
import com.krishimitra.crop.dto.request.UpdateFarmerCropRequest;
import com.krishimitra.crop.dto.response.FarmerCropResponse;

import java.util.List;

public interface FarmerCropService {

    FarmerCropResponse createCrop(
            CreateFarmerCropRequest request,
            String userId
    );

    List<FarmerCropResponse> getMyCrops(String userId);

    FarmerCropResponse getCropById(
            String cropId,
            String userId
    );

    FarmerCropResponse updateCrop(
            String cropId,
            UpdateFarmerCropRequest request,
            String userId
    );

    void deleteCrop(
            String cropId,
            String userId
    );
}