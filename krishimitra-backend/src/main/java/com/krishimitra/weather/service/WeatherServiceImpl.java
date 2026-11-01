package com.krishimitra.weather.service;

import tools.jackson.databind.JsonNode;
import com.krishimitra.farmer.model.FarmerProfile;
import com.krishimitra.farmer.repository.FarmerRepository;
import com.krishimitra.weather.client.OpenMeteoClient;
import com.krishimitra.weather.dto.WeatherLocation;
import com.krishimitra.weather.dto.WeatherResponse;
import com.krishimitra.weather.exception.WeatherLocationNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final String SOURCE = "Open-Meteo";
    private static final String ATTRIBUTION =
            "Weather data provided by Open-Meteo (CC BY 4.0)";

    private final OpenMeteoClient openMeteoClient;
    private final FarmerRepository farmerRepository;

    public WeatherServiceImpl(
            OpenMeteoClient openMeteoClient,
            FarmerRepository farmerRepository
    ) {
        this.openMeteoClient = openMeteoClient;
        this.farmerRepository = farmerRepository;
    }

    private WeatherLocation resolveLocation(
            String userId,
            Double latitude,
            Double longitude
    ) {
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException(
                    "Both latitude and longitude must be provided together"
            );
        }

        if (latitude != null) {
            if (latitude < -90 || latitude > 90
                    || longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException(
                        "Invalid latitude or longitude"
                );
            }

            return new WeatherLocation(
                    "GPS Location",
                    null,
                    null,
                    "India",
                    latitude,
                    longitude,
                    "auto"
            );
        }

        FarmerProfile farmer = farmerRepository.findByUserId(userId)
                .orElseThrow(() -> new WeatherLocationNotFoundException(
                        "Farmer profile not found. Please create your profile first."
                ));

        String taluka = farmer.getTaluka();
        String district = farmer.getDistrict();
        String state = farmer.getState();

        if (district == null || district.isBlank()) {
            throw new WeatherLocationNotFoundException(
                    "District is missing from your farmer profile"
            );
        }

        String searchQuery = (taluka != null && !taluka.isBlank())
                ? taluka + ", " + district + ", Karnataka"
                : district + ", Karnataka";

        JsonNode geocodingResponse =
                openMeteoClient.searchLocation(searchQuery);

        JsonNode results = geocodingResponse.path("results");

        // If the taluka is not found, retry using district.
        if (!results.isArray() || results.isEmpty()) {
            if (taluka != null && !taluka.isBlank()) {
                geocodingResponse = openMeteoClient.searchLocation(
                        district + ", Karnataka"
                );
                results = geocodingResponse.path("results");
            }
        }

        if (!results.isArray() || results.isEmpty()) {
            throw new WeatherLocationNotFoundException(
                    "Could not find weather coordinates for " + district
            );
        }

        JsonNode result = results.get(0);

        return new WeatherLocation(
                result.path("name").asText(),
                result.path("admin2").asText(district),
                result.path("admin1").asText(state),
                result.path("country").asText("India"),
                result.path("latitude").asDouble(),
                result.path("longitude").asDouble(),
                result.path("timezone").asText("Asia/Kolkata")
        );
    }

    private WeatherResponse buildWeatherResponse(
            String userId,
            Double latitude,
            Double longitude,
            String mode
    ) {
        WeatherLocation location = resolveLocation(
                userId,
                latitude,
                longitude
        );

        JsonNode weatherData = openMeteoClient.getWeather(
                location.latitude(),
                location.longitude(),
                mode
        );

        if (weatherData == null || weatherData.has("error")
                && weatherData.path("error").asBoolean()) {
            throw new com.krishimitra.weather.exception.WeatherDataException(
                    weatherData == null
                            ? "Empty weather response"
                            : weatherData.path("reason").asText(
                            "Weather provider returned an error"
                    )
            );
        }

        return new WeatherResponse(
                SOURCE,
                ATTRIBUTION,
                location,
                weatherData.path("current").isMissingNode()
                        ? null : weatherData.path("current"),
                weatherData.path("hourly").isMissingNode()
                        ? null : weatherData.path("hourly"),
                weatherData.path("daily").isMissingNode()
                        ? null : weatherData.path("daily"),
                Instant.now()
        );
    }

    @Override
    public WeatherResponse getCurrentWeather(
            String userId,
            Double latitude,
            Double longitude
    ) {
        return buildWeatherResponse(userId, latitude, longitude, "current");
    }

    @Override
    public WeatherResponse getForecast(
            String userId,
            Double latitude,
            Double longitude
    ) {
        return buildWeatherResponse(userId, latitude, longitude, "forecast");
    }

    @Override
    public WeatherResponse getHourlyWeather(
            String userId,
            Double latitude,
            Double longitude
    ) {
        return buildWeatherResponse(userId, latitude, longitude, "hourly");
    }

    @Override
    public WeatherResponse getWeatherLocation(
            String userId,
            Double latitude,
            Double longitude
    ) {
        return new WeatherResponse(
                SOURCE,
                ATTRIBUTION,
                resolveLocation(userId, latitude, longitude),
                null,
                null,
                null,
                Instant.now()
        );
    }
}