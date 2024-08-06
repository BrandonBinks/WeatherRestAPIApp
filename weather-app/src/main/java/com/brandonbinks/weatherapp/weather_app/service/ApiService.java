package com.brandonbinks.weatherapp.weather_app.service;

public interface ApiService {
    void validateApiKey(String apiKey);
    void enforceRateLimit(String apiKey);
}
