package com.krishimitra.weather.controller;

import com.krishimitra.auth.security.UserPrincipal;
import com.krishimitra.common.ApiResponse;
import com.krishimitra.weather.dto.WeatherResponse;
import com.krishimitra.weather.service.WeatherService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<WeatherResponse>> getCurrentWeather(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false)
            @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam(required = false)
            @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
    ) {
        WeatherResponse response = weatherService.getCurrentWeather(
                principal.getId(), latitude, longitude
        );

        return ResponseEntity.ok(
                ApiResponse.success("Current weather fetched successfully", response)
        );
    }

    @GetMapping("/forecast")
    public ResponseEntity<ApiResponse<WeatherResponse>> getForecast(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false)
            @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam(required = false)
            @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
    ) {
        WeatherResponse response = weatherService.getForecast(
                principal.getId(), latitude, longitude
        );

        return ResponseEntity.ok(
                ApiResponse.success("7-day forecast fetched successfully", response)
        );
    }

    @GetMapping("/hourly")
    public ResponseEntity<ApiResponse<WeatherResponse>> getHourlyWeather(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false)
            @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam(required = false)
            @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
    ) {
        WeatherResponse response = weatherService.getHourlyWeather(
                principal.getId(), latitude, longitude
        );

        return ResponseEntity.ok(
                ApiResponse.success("Hourly weather fetched successfully", response)
        );
    }

    @GetMapping("/location")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeatherLocation(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false)
            @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam(required = false)
            @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
    ) {
        WeatherResponse response = weatherService.getWeatherLocation(
                principal.getId(), latitude, longitude
        );

        return ResponseEntity.ok(
                ApiResponse.success("Weather location resolved successfully", response)
        );
    }
}