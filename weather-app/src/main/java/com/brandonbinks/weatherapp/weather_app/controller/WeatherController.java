package com.brandonbinks.weatherapp.weather_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.brandonbinks.weatherapp.weather_app.service.WeatherService;
import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class WeatherController {
    
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather")
    public WeatherResponse getWeather(@RequestParam String city,
                             @RequestParam String country,
                             @RequestParam String apiKey) {
        return weatherService.getWeather(city, country, apiKey);
    }
    
}
