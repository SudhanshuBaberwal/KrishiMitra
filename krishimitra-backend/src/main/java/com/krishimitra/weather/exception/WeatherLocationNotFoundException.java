package com.krishimitra.weather.exception;

public class WeatherLocationNotFoundException extends RuntimeException {

    public WeatherLocationNotFoundException(String message) {
        super(message);
    }
}