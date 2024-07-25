package com.brandonbinks.weatherapp.weather_app.exception;

public class MissingFieldException extends IllegalArgumentException {
    public MissingFieldException(String message) {
        super(message);
    }
    
}
