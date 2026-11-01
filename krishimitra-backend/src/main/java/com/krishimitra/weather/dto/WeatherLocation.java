package com.krishimitra.weather.dto;

public record WeatherLocation(
        String name,
        String district,
        String state,
        String country,
        Double latitude,
        Double longitude,
        String timezone
) {}