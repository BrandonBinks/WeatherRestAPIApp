package com.brandonbinks.weatherapp.weather_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.brandonbinks.weatherapp.weather_app.client.OpenWeatherMapClient;
import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;
import com.brandonbinks.weatherapp.weather_app.repository.WeatherRepository;

@SpringBootTest
public class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @MockBean
    private OpenWeatherMapClient openWeatherMapClient;

    @MockBean
    private WeatherRepository weatherRepository;
    
    @Test
    public void testGetWeather(){
        WeatherResponse.Weather weather = new WeatherResponse.Weather();
        weather.setDescription("clear sky");

        WeatherResponse response = new WeatherResponse();
        response.setWeather(List.of(weather));

        when(openWeatherMapClient.getWeather(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

        WeatherResponse result = weatherService.getWeather("London", "UK", "API_KEY_1");
        assertEquals("clear sky", result.getWeather().get(0).getDescription());
    }
}
