package com.krishimitra.soil.controller;

import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import com.krishimitra.soil.dto.request.CreateSoilRecordRequest;
import com.krishimitra.soil.dto.request.UpdateSoilRecordRequest;
import com.krishimitra.soil.dto.response.SoilRecordResponse;
import com.krishimitra.soil.service.SoilRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmers/me/soil-records")
public class SoilRecordController {

    private final SoilRecordService soilRecordService;

    public SoilRecordController(SoilRecordService soilRecordService) {
        this.soilRecordService = soilRecordService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SoilRecordResponse>> createSoilRecord(
            @Valid @RequestBody CreateSoilRecordRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SoilRecordResponse response = soilRecordService.createSoilRecord(
                request,
                principal.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Soil record added successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SoilRecordResponse>>> getMySoilRecords(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<SoilRecordResponse> response =
                soilRecordService.getMySoilRecords(principal.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Soil records fetched successfully", response)
        );
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<ApiResponse<SoilRecordResponse>> getSoilRecordById(
            @PathVariable String recordId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SoilRecordResponse response = soilRecordService.getSoilRecordById(
                recordId,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Soil record fetched successfully", response)
        );
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<ApiResponse<SoilRecordResponse>> updateSoilRecord(
            @PathVariable String recordId,
            @Valid @RequestBody UpdateSoilRecordRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SoilRecordResponse response = soilRecordService.updateSoilRecord(
                recordId,
                request,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Soil record updated successfully", response)
        );
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Void>> deleteSoilRecord(
            @PathVariable String recordId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        soilRecordService.deleteSoilRecord(
                recordId,
                principal.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Soil record deactivated successfully", null)
        );
    }
}