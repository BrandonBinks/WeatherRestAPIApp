package com.brandonbinks.weatherapp.weather_app.service;

import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeather(String city, String country, String apiKey);
    void validateFields(String city, String country);
    void updateWeatherData(String city, String country, WeatherResponse response);
    WeatherResponse checkDatabaseForWeather(String city, String country);

}
