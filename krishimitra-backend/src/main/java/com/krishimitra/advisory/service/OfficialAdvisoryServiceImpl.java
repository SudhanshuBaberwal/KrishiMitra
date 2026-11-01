package com.krishimitra.advisory.service;

import com.krishimitra.advisory.dto.AdvisorySearchRequest;
import com.krishimitra.advisory.dto.request.CreateAdvisoryRequest;
import com.krishimitra.advisory.dto.response.AdvisoryResponse;
import com.krishimitra.advisory.entity.OfficialAdvisory;
import com.krishimitra.advisory.exception.AdvisoryNotFoundException;
import com.krishimitra.advisory.exception.InvalidAdvisoryAccessException;
import com.krishimitra.advisory.repository.OfficialAdvisoryRepository;
import com.krishimitra.crop.entity.FarmerCrop;
import com.krishimitra.crop.repository.FarmerCropRepository;
import com.krishimitra.farmer.model.FarmerProfile;
import com.krishimitra.farmer.repository.FarmerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfficialAdvisoryServiceImpl
        implements OfficialAdvisoryService {

    private final OfficialAdvisoryRepository advisoryRepository;
    private final FarmerRepository farmerRepository;
    private final FarmerCropRepository farmerCropRepository;

    public OfficialAdvisoryServiceImpl(
            OfficialAdvisoryRepository advisoryRepository,
            FarmerRepository farmerRepository,
            FarmerCropRepository farmerCropRepository) {

        this.advisoryRepository = advisoryRepository;
        this.farmerRepository = farmerRepository;
        this.farmerCropRepository = farmerCropRepository;
    }

    @Override
    public AdvisoryResponse publishAdvisory(
            CreateAdvisoryRequest request) {

        if (request.validUntil().isBefore(request.validFrom())) {
            throw new IllegalArgumentException(
                    "validUntil must be on or after validFrom"
            );
        }

        OfficialAdvisory advisory = new OfficialAdvisory();

        advisory.setState(request.state().trim());
        advisory.setDistrict(request.district().trim());
        advisory.setSource(request.source());
        advisory.setSourceName(request.sourceName().trim());
        advisory.setSourceReference(request.sourceReference());
        advisory.setSourceUrl(request.sourceUrl().trim());
        advisory.setTitle(request.title().trim());
        advisory.setSummary(request.summary().trim());
        advisory.setContent(request.content().trim());
        advisory.setCategory(request.category());
        advisory.setLanguage(request.language());

        advisory.setCropCodes(
                request.cropCodes() == null
                        ? List.of()
                        : request.cropCodes().stream()
                          .filter(code -> code != null && !code.isBlank())
                          .map(String::trim)
                          .distinct()
                          .toList()
        );

        advisory.setPublishedAt(request.publishedAt());
        advisory.setValidFrom(request.validFrom());
        advisory.setValidUntil(request.validUntil());

        // This endpoint is protected by the private ingestion key.
        advisory.setVerified(true);
        advisory.setActive(true);

        Instant now = Instant.now();
        advisory.setCreatedAt(now);
        advisory.setUpdatedAt(now);

        return toResponse(advisoryRepository.save(advisory));
    }

    @Override
    public List<AdvisoryResponse> getFarmerAdvisories(
            String userId,
            AdvisorySearchRequest request) {

        FarmerProfile farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidAdvisoryAccessException(
                        "Farmer profile not found. Please complete your profile first."
                ));

        if (farmer.getDistrict() == null || farmer.getState() == null) {
            throw new InvalidAdvisoryAccessException(
                    "Farmer district and state are required to find advisories."
            );
        }

        LocalDate today = LocalDate.now();

        List<OfficialAdvisory> advisories =
                advisoryRepository
                        .findByActiveTrueAndDistrictIgnoreCaseAndStateIgnoreCaseAndValidUntilGreaterThanEqualOrderByPublishedAtDesc(
                                farmer.getDistrict(),
                                farmer.getState(),
                                today
                        );

        Set<String> farmerCropCodes =
                farmerCropRepository
                        .findByFarmerIdAndActiveTrueOrderByCreatedAtDesc(
                                farmer.getId()
                        )
                        .stream()
                        .map(FarmerCrop::getCropCode)
                        .filter(code -> code != null && !code.isBlank())
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());

        return advisories.stream()
                .filter(advisory ->
                        !advisory.getValidFrom().isAfter(today)
                )
                .filter(advisory ->
                        matchesCrop(advisory, farmerCropCodes, request)
                )
                .filter(advisory ->
                        request == null
                                || request.category() == null
                                || advisory.getCategory() == request.category()
                )
                .filter(advisory ->
                        request == null
                                || request.language() == null
                                || advisory.getLanguage() == request.language()
                )
                .filter(OfficialAdvisory::isVerified)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AdvisoryResponse getAdvisoryById(
            String id,
            String userId) {

        FarmerProfile farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidAdvisoryAccessException(
                        "Farmer profile not found."
                ));

        OfficialAdvisory advisory = advisoryRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new AdvisoryNotFoundException(
                        "Advisory not found: " + id
                ));

        if (!advisory.getDistrict().equalsIgnoreCase(farmer.getDistrict())
                || !advisory.getState().equalsIgnoreCase(farmer.getState())) {
            throw new InvalidAdvisoryAccessException(
                    "This advisory is not available for your district."
            );
        }

        if (!advisory.isVerified()
                || advisory.getValidFrom().isAfter(LocalDate.now())
                || advisory.getValidUntil().isBefore(LocalDate.now())) {
            throw new AdvisoryNotFoundException(
                    "This advisory is not currently valid."
            );
        }

        return toResponse(advisory);
    }

    private boolean matchesCrop(
            OfficialAdvisory advisory,
            Set<String> farmerCropCodes,
            AdvisorySearchRequest request) {

        List<String> advisoryCrops = advisory.getCropCodes();

        // Empty cropCodes means the advisory applies to all crops.
        if (advisoryCrops == null || advisoryCrops.isEmpty()) {
            return request == null
                    || request.cropCode() == null
                    || request.cropCode().isBlank();
        }

        if (request != null
                && request.cropCode() != null
                && !request.cropCode().isBlank()) {

            return advisoryCrops.stream()
                    .anyMatch(code ->
                            code.equalsIgnoreCase(request.cropCode().trim())
                    );
        }

        return advisoryCrops.stream()
                .anyMatch(code ->
                        farmerCropCodes.contains(code.toLowerCase())
                );
    }

    private AdvisoryResponse toResponse(OfficialAdvisory advisory) {

        return new AdvisoryResponse(
                advisory.getId(),
                advisory.getState(),
                advisory.getDistrict(),
                advisory.getSource(),
                advisory.getSourceName(),
                advisory.getSourceReference(),
                advisory.getSourceUrl(),
                advisory.getTitle(),
                advisory.getSummary(),
                advisory.getContent(),
                advisory.getCategory(),
                advisory.getLanguage(),
                advisory.getCropCodes(),
                advisory.getPublishedAt(),
                advisory.getValidFrom(),
                advisory.getValidUntil(),
                advisory.isVerified(),
                advisory.getUpdatedAt()
        );
    }
}