package com.krishimitra.dashboard.dto.response;

import com.krishimitra.weather.dto.WeatherLocation;
import tools.jackson.databind.JsonNode;

import java.time.Instant;

public record WeatherSummary(
        String source,
        String attribution,
        WeatherLocation location,
        JsonNode current,
        JsonNode daily,
        Instant fetchedAt
) {}