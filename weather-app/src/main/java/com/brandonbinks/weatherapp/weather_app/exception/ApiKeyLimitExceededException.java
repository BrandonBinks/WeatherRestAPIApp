package com.brandonbinks.weatherapp.weather_app.exception;

public class ApiKeyLimitExceededException extends IllegalArgumentException {
    public ApiKeyLimitExceededException(String message) {
        super(message);
    }
}
