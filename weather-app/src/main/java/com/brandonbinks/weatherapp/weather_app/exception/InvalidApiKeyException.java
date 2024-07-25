package com.brandonbinks.weatherapp.weather_app.exception;

public class InvalidApiKeyException extends IllegalArgumentException {
    public InvalidApiKeyException(String message) {
        super(message);
    }
}
