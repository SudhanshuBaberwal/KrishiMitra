package com.krishimitra.weather.client;

import tools.jackson.databind.JsonNode;
import com.krishimitra.weather.exception.WeatherDataException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenMeteoClient {

    private final RestClient weatherClient;
    private final RestClient geocodingClient;

    public OpenMeteoClient(
            RestClient openMeteoRestClient,
            RestClient openMeteoGeocodingRestClient
    ) {
        this.weatherClient = openMeteoRestClient;
        this.geocodingClient = openMeteoGeocodingRestClient;
    }

    public JsonNode searchLocation(String location) {
        try {
            JsonNode response = geocodingClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search")
                            .queryParam("name", location)
                            .queryParam("count", 1)
                            .queryParam("language", "en")
                            .queryParam("format", "json")
                            .queryParam("countryCode", "IN")
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                throw new WeatherDataException(
                        "Empty response from Open-Meteo geocoding API"
                );
            }

            return response;

        } catch (RestClientException ex) {
            throw new WeatherDataException(
                    "Unable to resolve weather location",
                    ex
            );
        }
    }

    public JsonNode getWeather(
            double latitude,
            double longitude,
            String mode
    ) {
        try {
            return weatherClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .path("/v1/forecast")
                                .queryParam("latitude", latitude)
                                .queryParam("longitude", longitude)
                                .queryParam("timezone", "auto")
                                .queryParam("temperature_unit", "celsius")
                                .queryParam("wind_speed_unit", "kmh")
                                .queryParam("precipitation_unit", "mm");

                        switch (mode) {
                            case "current" -> uriBuilder
                                    .queryParam("current",
                                            "temperature_2m,relative_humidity_2m,"
                                                    + "apparent_temperature,is_day,precipitation,"
                                                    + "rain,showers,weather_code,cloud_cover,"
                                                    + "wind_speed_10m,wind_direction_10m,"
                                                    + "wind_gusts_10m");

                            case "hourly" -> uriBuilder
                                    .queryParam("hourly",
                                            "temperature_2m,relative_humidity_2m,"
                                                    + "apparent_temperature,precipitation_probability,"
                                                    + "precipitation,rain,weather_code,"
                                                    + "wind_speed_10m");

                            case "forecast" -> {
                                uriBuilder
                                        .queryParam("current",
                                                "temperature_2m,relative_humidity_2m,"
                                                        + "apparent_temperature,precipitation,"
                                                        + "weather_code,wind_speed_10m")
                                        .queryParam("daily",
                                                "weather_code,temperature_2m_max,"
                                                        + "temperature_2m_min,rain_sum,"
                                                        + "precipitation_sum,"
                                                        + "precipitation_probability_max,"
                                                        + "wind_speed_10m_max,"
                                                        + "sunrise,sunset")
                                        .queryParam("forecast_days", 7);
                            }

                            default -> throw new IllegalArgumentException(
                                    "Unsupported weather mode: " + mode
                            );
                        }

                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(JsonNode.class);

        } catch (RestClientException ex) {
            throw new WeatherDataException(
                    "Unable to fetch weather data from Open-Meteo",
                    ex
            );
        }
    }
}