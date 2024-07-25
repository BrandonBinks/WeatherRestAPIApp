package com.brandonbinks.weatherapp.weather_app.client;

import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "openWeatherMapClient", url = "${openweathermap.api.url}")
public interface OpenWeatherMapClient {

    @GetMapping("/data/2.5/weather")
    WeatherResponse getWeather(@RequestParam("q") String cityCountry, @RequestParam("appid") String apiKey);
    
}
