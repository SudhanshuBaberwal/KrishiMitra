
package com.krishimitra.ai.service;

import com.krishimitra.advisory.dto.AdvisorySearchRequest;
import com.krishimitra.advisory.dto.response.AdvisoryResponse;
import com.krishimitra.advisory.enums.AdvisoryLanguage;
import com.krishimitra.advisory.service.OfficialAdvisoryService;

import com.krishimitra.crop.entity.FarmerCrop;
import com.krishimitra.crop.repository.FarmerCropRepository;

import com.krishimitra.farmer.model.FarmerProfile;
import com.krishimitra.farmer.repository.FarmerRepository;

import com.krishimitra.soil.dto.response.SoilRecordResponse;
import com.krishimitra.soil.service.SoilRecordService;

import com.krishimitra.weather.dto.WeatherResponse;
import com.krishimitra.weather.service.WeatherService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class FarmerContextService {

    private static final Logger log =
            LoggerFactory.getLogger(FarmerContextService.class);

    private final FarmerRepository farmerRepository;
    private final FarmerCropRepository farmerCropRepository;
    private final SoilRecordService soilRecordService;
    private final WeatherService weatherService;
    private final OfficialAdvisoryService advisoryService;

    public FarmerContextService(
            FarmerRepository farmerRepository,
            FarmerCropRepository farmerCropRepository,
            SoilRecordService soilRecordService,
            WeatherService weatherService,
            OfficialAdvisoryService advisoryService
    ) {
        this.farmerRepository = farmerRepository;
        this.farmerCropRepository = farmerCropRepository;
        this.soilRecordService = soilRecordService;
        this.weatherService = weatherService;
        this.advisoryService = advisoryService;
    }

    public String buildContext(String userId, String userMessage) {

        FarmerProfile farmer = farmerRepository
                .findByUserId(userId)
                .orElse(null);

        if (farmer == null) {
            return """
                    FARMER CONTEXT:
                    Farmer profile is not available.
                    Do not assume the farmer's location, crops or soil data.
                    """;
        }

        StringBuilder context = new StringBuilder();

        context.append("CURRENT DATE: ")
                .append(LocalDate.now())
                .append("\n\n");

        // 1. Farmer profile
        appendFarmerProfile(context, farmer);

        // 2. Registered active crops
        appendCrops(context, farmer);

        // 3. Latest active soil record
        appendSoilInformation(context, userId);

        // 4. Weather: only fetch when relevant to the question
        if (isWeatherRelated(userMessage)) {
            appendWeatherInformation(context, userId);
        }

        // 5. Current verified and location-matched advisories
        appendAdvisories(context, userId, farmer);

        return context.toString();
    }

    private void appendFarmerProfile(
            StringBuilder context,
            FarmerProfile farmer
    ) {
        context.append("FARMER PROFILE:\n");
        context.append("Name: ").append(value(farmer.getName())).append("\n");
        context.append("State: ").append(value(farmer.getState())).append("\n");
        context.append("District: ").append(value(farmer.getDistrict())).append("\n");
        context.append("Taluka: ").append(value(farmer.getTaluka())).append("\n");
        context.append("Preferred language: ")
                .append(value(farmer.getPreferredLanguage())).append("\n");
        context.append("Farm size: ").append(value(farmer.getFarmSize())).append("\n");
        context.append("Irrigation type: ")
                .append(value(farmer.getIrrigationType())).append("\n\n");
    }

    private void appendCrops(
            StringBuilder context,
            FarmerProfile farmer
    ) {
        List<FarmerCrop> crops =
                farmerCropRepository
                        .findByFarmerIdAndActiveTrueOrderByCreatedAtDesc(
                                farmer.getId()
                        );

        context.append("REGISTERED ACTIVE CROPS:\n");

        if (crops.isEmpty()) {
            context.append("No active crops registered.\n\n");
            return;
        }

        for (FarmerCrop crop : crops) {
            context.append("Crop name: ")
                    .append(value(crop.getCropName())).append("\n");

            context.append("Crop code: ")
                    .append(value(crop.getCropCode())).append("\n");

            context.append("Variety: ")
                    .append(value(crop.getVariety())).append("\n");

            context.append("Season: ")
                    .append(value(crop.getSeason())).append("\n");

            context.append("Sowing date: ")
                    .append(value(crop.getSowingDate())).append("\n");

            context.append("Growth stage: ")
                    .append(value(crop.getGrowthStage())).append("\n");

            context.append("Cultivated area: ")
                    .append(value(crop.getArea())).append(" ")
                    .append(value(crop.getAreaUnit())).append("\n");

            context.append("Expected harvest date: ")
                    .append(value(crop.getExpectedHarvestDate()))
                    .append("\n\n");
        }
    }

    private void appendSoilInformation(
            StringBuilder context,
            String userId
    ) {
        try {
            List<SoilRecordResponse> records =
                    soilRecordService.getMySoilRecords(userId);

            context.append("LATEST SOIL INFORMATION:\n");

            if (records == null || records.isEmpty()) {
                context.append("No active soil records available.\n\n");
                return;
            }

            // SoilRecordService already returns active records
            // ordered by sampleDate descending.
            SoilRecordResponse latest = records.getFirst();

            context.append("Sample date: ")
                    .append(value(latest.sampleDate())).append("\n");

            context.append("Sample location: ")
                    .append(value(latest.sampleLocation())).append("\n");

            context.append("Data source: ")
                    .append(value(latest.source())).append("\n");

            context.append("Verification status: ")
                    .append(value(latest.verificationStatus())).append("\n");

            context.append("Laboratory: ")
                    .append(value(latest.labName())).append("\n");

            context.append("Soil parameters:\n")
                    .append(value(latest.parameters())).append("\n");

            context.append(
                    "IMPORTANT: Consider the sample date and verification "
                            + "status when interpreting these values. "
                            + "Do not treat self-reported values as laboratory-verified.\n\n"
            );

        } catch (Exception ex) {
            log.warn(
                    "Could not load soil context for userId={}",
                    userId,
                    ex
            );

            context.append(
                    "Soil information is temporarily unavailable.\n\n"
            );
        }
    }

    private void appendWeatherInformation(
            StringBuilder context,
            String userId
    ) {
        try {
            WeatherResponse weather =
                    weatherService.getForecast(userId, null, null);

            context.append("WEATHER FORECAST:\n");

            if (weather == null) {
                context.append("Weather data is unavailable.\n\n");
                return;
            }

            context.append("Provider: ")
                    .append(value(weather.source())).append("\n");

            context.append("Attribution: ")
                    .append(value(weather.attribution())).append("\n");

            context.append("Location: ")
                    .append(value(weather.location())).append("\n");

            context.append("Fetched at: ")
                    .append(value(weather.fetchedAt())).append("\n");

            context.append("Current conditions:\n")
                    .append(value(weather.current())).append("\n");

            context.append("Daily forecast:\n")
                    .append(value(weather.daily())).append("\n\n");

            context.append(
                    "IMPORTANT: Use the supplied forecast data only. "
                            + "Mention its location and fetched time when relevant. "
                            + "Do not invent missing forecast values.\n\n"
            );

        } catch (Exception ex) {
            log.warn(
                    "Could not load weather context for userId={}",
                    userId,
                    ex
            );

            context.append(
                    "Weather information is temporarily unavailable. "
                            + "Do not invent current weather or forecasts.\n\n"
            );
        }
    }

    private void appendAdvisories(
            StringBuilder context,
            String userId,
            FarmerProfile farmer
    ) {
        try {
            List<AdvisoryResponse> advisories =
                    advisoryService.getFarmerAdvisories(
                            userId,
                            new AdvisorySearchRequest(null, null, null)
                    );

            context.append("MATCHING AGRICULTURAL ADVISORIES:\n");

            if (advisories == null || advisories.isEmpty()) {
                context.append(
                        "No currently matching advisories are available.\n\n"
                );
                return;
            }

            for (AdvisoryResponse advisory : advisories) {

                context.append("Title: ")
                        .append(value(advisory.title())).append("\n");

                context.append("Summary: ")
                        .append(value(advisory.summary())).append("\n");

                context.append("Content: ")
                        .append(value(advisory.content())).append("\n");

                context.append("Source: ")
                        .append(value(advisory.source())).append("\n");

                context.append("Source name: ")
                        .append(value(advisory.sourceName())).append("\n");

                context.append("Source URL: ")
                        .append(value(advisory.sourceUrl())).append("\n");

                context.append("Category: ")
                        .append(value(advisory.category())).append("\n");

                context.append("Language: ")
                        .append(value(advisory.language())).append("\n");

                context.append("Crop codes: ")
                        .append(value(advisory.cropCodes())).append("\n");

                context.append("Published date: ")
                        .append(value(advisory.publishedAt())).append("\n");

                context.append("Valid until: ")
                        .append(value(advisory.validUntil())).append("\n");

                context.append("Admin verification flag: ")
                        .append(advisory.verified()).append("\n\n");
            }

            context.append(
                    "IMPORTANT: These advisories were returned by the "
                            + "application's location and validity filtering. "
                            + "The verification flag represents the application's "
                            + "admin review status; it is not independent proof "
                            + "that the source content is authentic. "
                            + "Do not invent or alter advisory instructions.\n\n"
            );

        } catch (Exception ex) {
            log.warn(
                    "Could not load advisory context for userId={}",
                    userId,
                    ex
            );

            context.append(
                    "Official advisory information is temporarily unavailable. "
                            + "Do not invent official advisories.\n\n"
            );
        }
    }

    private boolean isWeatherRelated(String message) {

        if (message == null || message.isBlank()) {
            return false;
        }

        String text = message.toLowerCase(Locale.ROOT);

        return text.matches(
                "(?s).*\\b(weather|forecast|rain|rainfall|temperature|"
                        + "humidity|wind|drought|irrigation|watering|"
                        + "monsoon|climate)\\b.*"
        )
                || text.matches(
                "(?s).*(मौसम|बारिश|वर्षा|तापमान|नमी|हवा|सिंचाई|"
                        + "मानसून|सूखा|ಮಳೆ|ಹವಾಮಾನ|ತಾಪಮಾನ|ತೇವಾಂಶ|"
                        + "ಗಾಳಿ|ನೀರಾವರಿ).*"
        );
    }

    private String value(Object value) {
        return value == null ? "Not available" : value.toString();
    }
}
