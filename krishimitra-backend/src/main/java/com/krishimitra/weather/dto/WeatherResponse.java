package com.krishimitra.weather.dto;

import tools.jackson.databind.JsonNode;

import java.time.Instant;

public record WeatherResponse(
        String source,
        String attribution,
        WeatherLocation location,
        JsonNode current,
        JsonNode hourly,
        JsonNode daily,
        Instant fetchedAt
) {}