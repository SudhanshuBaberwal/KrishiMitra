package com.krishimitra.advisory.controller;

import com.krishimitra.advisory.dto.AdvisorySearchRequest;
import com.krishimitra.advisory.dto.request.CreateAdvisoryRequest;
import com.krishimitra.advisory.dto.response.AdvisoryResponse;
import com.krishimitra.advisory.service.OfficialAdvisoryService;
import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping("/api/advisories")
public class OfficialAdvisoryController {

    private final OfficialAdvisoryService advisoryService;
    private final String ingestionKey;

    public OfficialAdvisoryController(
            OfficialAdvisoryService advisoryService,
            @Value("${app.advisory.ingestion-key}")
            String ingestionKey) {

        this.advisoryService = advisoryService;
        this.ingestionKey = ingestionKey;
    }

    // Private publishing endpoint.
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<AdvisoryResponse>> publishAdvisory(
            @RequestHeader("X-ADVISORY-INGEST-KEY") String suppliedKey,
            @Valid @RequestBody CreateAdvisoryRequest request) {

        if (!isValidIngestionKey(suppliedKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(
                            "Invalid advisory ingestion key.",
                            null
                    ));
        }

        AdvisoryResponse response =
                advisoryService.publishAdvisory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Official advisory published successfully.",
                        response
                ));
    }

    // Farmer's district and crop-based advisories.
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdvisoryResponse>>> getAdvisories(
            @AuthenticationPrincipal UserPrincipal principal,
            @ModelAttribute AdvisorySearchRequest request) {

        List<AdvisoryResponse> advisories =
                advisoryService.getFarmerAdvisories(
                        principal.getId(),
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Official advisories fetched successfully.",
                        advisories
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvisoryResponse>> getAdvisoryById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        AdvisoryResponse advisory =
                advisoryService.getAdvisoryById(
                        id,
                        principal.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Advisory fetched successfully.",
                        advisory
                )
        );
    }

    private boolean isValidIngestionKey(String suppliedKey) {

        if (suppliedKey == null
                || ingestionKey == null
                || ingestionKey.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                ingestionKey.getBytes(StandardCharsets.UTF_8),
                suppliedKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}