
package com.krishimitra.farmer.controller;

import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import com.krishimitra.farmer.dto.request.CreateFarmerRequest;
import com.krishimitra.farmer.dto.request.UpdateFarmerRequest;
import com.krishimitra.farmer.dto.response.FarmerResponse;
import com.krishimitra.farmer.service.FarmerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmers")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    // Create farmer profile
    @PostMapping
    public ResponseEntity<ApiResponse<FarmerResponse>> createFarmer(
            @Valid @RequestBody CreateFarmerRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        FarmerResponse response = farmerService.createFarmer(
                request,
                principal.getId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Farmer profile created successfully",
                        response
                ));
    }

    // Get currently authenticated farmer's profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<FarmerResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        FarmerResponse response = farmerService.getFarmerByUserId(
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Farmer profile fetched successfully",
                        response
                )
        );
    }


    @PutMapping("/me")
    public ResponseEntity<ApiResponse<FarmerResponse>> updateMyProfile(
            @Valid @RequestBody UpdateFarmerRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        FarmerResponse response = farmerService.updateFarmer(
                request,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Farmer profile updated successfully",
                        response
                )
        );
    }
}
