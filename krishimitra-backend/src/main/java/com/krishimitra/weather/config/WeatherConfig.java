package com.krishimitra.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WeatherConfig {

    @Bean
    public RestClient openMeteoRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    @Bean
    public RestClient openMeteoGeocodingRestClient() {
        return RestClient.builder()
                .baseUrl("https://geocoding-api.open-meteo.com")
                .build();
    }
}