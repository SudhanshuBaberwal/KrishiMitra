package com.krishimitra.weather.service;

import com.krishimitra.weather.dto.WeatherResponse;

public interface WeatherService {

    WeatherResponse getCurrentWeather(
            String userId,
            Double latitude,
            Double longitude
    );

    WeatherResponse getForecast(
            String userId,
            Double latitude,
            Double longitude
    );

    WeatherResponse getHourlyWeather(
            String userId,
            Double latitude,
            Double longitude
    );

    WeatherResponse getWeatherLocation(
            String userId,
            Double latitude,
            Double longitude
    );
}