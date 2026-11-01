package com.krishimitra.dashboard.service;

import com.krishimitra.advisory.dto.AdvisorySearchRequest;
import com.krishimitra.advisory.dto.response.AdvisoryResponse;
import com.krishimitra.advisory.service.OfficialAdvisoryService;

import com.krishimitra.crop.dto.response.FarmerCropResponse;
import com.krishimitra.crop.service.FarmerCropService;

import com.krishimitra.dashboard.dto.response.*;
import com.krishimitra.farmer.dto.response.FarmerResponse;
import com.krishimitra.farmer.service.FarmerService;

import com.krishimitra.soil.dto.response.SoilRecordResponse;
import com.krishimitra.soil.service.SoilRecordService;

import com.krishimitra.weather.dto.WeatherResponse;
import com.krishimitra.weather.service.WeatherService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log =
            LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final FarmerService farmerService;
    private final FarmerCropService farmerCropService;
    private final SoilRecordService soilRecordService;
    private final WeatherService weatherService;
    private final OfficialAdvisoryService advisoryService;

    public DashboardServiceImpl(
            FarmerService farmerService,
            FarmerCropService farmerCropService,
            SoilRecordService soilRecordService,
            WeatherService weatherService,
            OfficialAdvisoryService advisoryService
    ) {
        this.farmerService = farmerService;
        this.farmerCropService = farmerCropService;
        this.soilRecordService = soilRecordService;
        this.weatherService = weatherService;
        this.advisoryService = advisoryService;
    }

    @Override
    public DashboardOverviewResponse getDashboardOverview(
            String userId
    ) {

        List<String> warnings = new ArrayList<>();

        // 1. Farmer profile
        FarmerResponse farmer =
                farmerService.getFarmerByUserId(userId);

        FarmerSummary farmerSummary = mapFarmer(farmer);

        // 2. Active crops
        List<FarmerCropResponse> farmerCrops =
                farmerCropService.getMyCrops(userId);

        List<CropSummary> crops = farmerCrops.stream()
                .filter(FarmerCropResponse::active)
                .map(this::mapCrop)
                .toList();

        // 3. Latest soil record
        SoilSummary latestSoil = null;

        try {
            List<SoilRecordResponse> soilRecords =
                    soilRecordService.getMySoilRecords(userId);

            if (soilRecords != null && !soilRecords.isEmpty()) {
                latestSoil = mapSoil(soilRecords.getFirst());
            }

        } catch (RuntimeException ex) {
            log.warn(
                    "Unable to load soil records for dashboard. userId={}",
                    userId,
                    ex
            );

            warnings.add("Soil information is temporarily unavailable.");
        }

        // 4. Weather forecast
        WeatherSummary weather = null;

        try {
            WeatherResponse weatherResponse =
                    weatherService.getForecast(userId, null, null);

            weather = mapWeather(weatherResponse);

        } catch (RuntimeException ex) {
            log.warn(
                    "Unable to load weather for dashboard. userId={}",
                    userId,
                    ex
            );

            warnings.add("Weather information is temporarily unavailable.");
        }

        // 5. Official agricultural advisories
        List<AdvisorySummary> advisories = List.of();

        try {
            List<AdvisoryResponse> advisoryResponses =
                    advisoryService.getFarmerAdvisories(
                            userId,
                            new AdvisorySearchRequest(null, null, null)
                    );

            if (advisoryResponses != null) {
                advisories = advisoryResponses.stream()
                        .map(this::mapAdvisory)
                        .toList();
            }

        } catch (RuntimeException ex) {
            log.warn(
                    "Unable to load advisories for dashboard. userId={}",
                    userId,
                    ex
            );

            warnings.add(
                    "Official agricultural advisories are temporarily unavailable."
            );
        }

        return new DashboardOverviewResponse(
                farmerSummary,
                crops,
                latestSoil,
                weather,
                advisories,
                warnings,
                Instant.now()
        );
    }

    private FarmerSummary mapFarmer(FarmerResponse farmer) {

        return new FarmerSummary(
                farmer.id(),
                farmer.name(),
                farmer.state(),
                farmer.district(),
                farmer.taluka(),
                farmer.preferredLanguage(),
                farmer.farmSize(),
                farmer.irrigationType()
        );
    }

    private CropSummary mapCrop(FarmerCropResponse crop) {

        return new CropSummary(
                crop.id(),
                crop.cropCode(),
                crop.cropName(),
                crop.variety(),
                crop.season(),
                crop.sowingDate(),
                crop.growthStage(),
                crop.area(),
                crop.areaUnit(),
                crop.expectedHarvestDate()
        );
    }

    private SoilSummary mapSoil(SoilRecordResponse soil) {

        return new SoilSummary(
                soil.id(),
                soil.sampleDate(),
                soil.sampleLocation(),
                soil.source(),
                soil.verificationStatus(),
                soil.labName(),
                soil.parameters(),
                soil.notes()
        );
    }

    private WeatherSummary mapWeather(WeatherResponse weather) {

        if (weather == null) {
            return null;
        }

        return new WeatherSummary(
                weather.source(),
                weather.attribution(),
                weather.location(),
                weather.current(),
                weather.daily(),
                weather.fetchedAt()
        );
    }

    private AdvisorySummary mapAdvisory(
            AdvisoryResponse advisory
    ) {

        return new AdvisorySummary(
                advisory.id(),
                advisory.title(),
                advisory.summary(),
                advisory.source(),
                advisory.sourceName(),
                advisory.sourceUrl(),
                advisory.category(),
                advisory.language(),
                advisory.cropCodes(),
                advisory.publishedAt(),
                advisory.validFrom(),
                advisory.validUntil(),
                advisory.verified()
        );
    }
}