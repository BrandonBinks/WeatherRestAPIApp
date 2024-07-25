package com.brandonbinks.weatherapp.weather_app.service;

import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeather(String city, String country, String apiKey);

}
