package com.krishimitra.crop.controller;

import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import com.krishimitra.crop.dto.request.CreateFarmerCropRequest;
import com.krishimitra.crop.dto.request.UpdateFarmerCropRequest;
import com.krishimitra.crop.dto.response.FarmerCropResponse;
import com.krishimitra.crop.service.FarmerCropService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmers/me/crops")
public class FarmerCropController {

    private final FarmerCropService farmerCropService;

    public FarmerCropController(
            FarmerCropService farmerCropService
    ) {
        this.farmerCropService = farmerCropService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FarmerCropResponse>> createCrop(
            @Valid @RequestBody CreateFarmerCropRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        FarmerCropResponse response = farmerCropService.createCrop(
                request,
                principal.getId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Crop added successfully",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FarmerCropResponse>>> getMyCrops(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        List<FarmerCropResponse> response =
                farmerCropService.getMyCrops(principal.getId());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Crops fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/{cropId}")
    public ResponseEntity<ApiResponse<FarmerCropResponse>> getCropById(
            @PathVariable String cropId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        FarmerCropResponse response = farmerCropService.getCropById(
                cropId,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Crop fetched successfully",
                        response
                )
        );
    }

    @PutMapping("/{cropId}")
    public ResponseEntity<ApiResponse<FarmerCropResponse>> updateCrop(
            @PathVariable String cropId,
            @Valid @RequestBody UpdateFarmerCropRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        FarmerCropResponse response = farmerCropService.updateCrop(
                cropId,
                request,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Crop updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{cropId}")
    public ResponseEntity<ApiResponse<Void>> deleteCrop(
            @PathVariable String cropId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        farmerCropService.deleteCrop(
                cropId,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Crop deactivated successfully",
                        null
                )
        );
    }
}